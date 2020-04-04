package wdawson.samples.revoker.health;

import com.codahale.metrics.health.HealthCheck;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.util.List;
import wdawson.samples.revoker.representations.CRLNameFilePair;

/**
 * Handles health checks for the CRL Distribution Point
 *
 * @author wdawson
 */
public class CRLHealthCheck extends HealthCheck {

    private List<CRLNameFilePair> crlFiles;

    private final CertificateFactory certificateFactory;

    public CRLHealthCheck(List<CRLNameFilePair> crlFiles) throws Exception {
        this.crlFiles = crlFiles;
        this.certificateFactory = CertificateFactory.getInstance("X.509");
    }

    @Override
    protected Result check() {
        try {
            checkThatAllCRLFilesAreReadable();
            return Result.healthy();
        } catch (Exception e) {
            return Result.unhealthy(e);
        }
    }

    private void checkThatAllCRLFilesAreReadable() {
        crlFiles.forEach(pair -> makeCRL(pair.getFilePath()));
    }

    private void makeCRL(String fileName) {
        try (InputStream crlStream = new FileInputStream(fileName)) {
            certificateFactory.generateCRL(crlStream);
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse CRL: " + fileName, e);
        }
    }
}
