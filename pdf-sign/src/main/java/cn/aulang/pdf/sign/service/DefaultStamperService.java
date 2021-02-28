package cn.aulang.pdf.sign.service;

import cn.aulang.pdf.sign.StamperService;
import cn.aulang.pdf.sign.model.KeyAndCert;
import cn.aulang.pdf.sign.util.KeyStoreUtils;
import cn.aulang.pdf.sign.util.KeyStoreUtils.KeyStoreFormat;
import com.itextpdf.signatures.DigestAlgorithms;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * @author Aulang
 * @date 2021-01-17 10:56
 */
public class DefaultStamperService implements StamperService {
    private static byte[] stamper = null;
    private static KeyAndCert keyAndCert = null;

    private final String stamperPath;
    private final String keyStorePath;
    private final String keyStorePassword;
    private final KeyStoreFormat keyStoreFormat;

    public DefaultStamperService(KeyStoreFormat keyStoreFormat,
                                 String keyStorePath,
                                 String keyStorePassword,
                                 String stamperPath) {
        this.keyStoreFormat = keyStoreFormat;
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.stamperPath = stamperPath;
    }

    private synchronized void initKeyAndCert() {
        if (keyAndCert != null) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(keyStorePath)) {
            keyAndCert = KeyStoreUtils.extractKeyAndCert(keyStoreFormat, fis, keyStorePassword);
        } catch (Exception e) {
            throw new RuntimeException("加载证书存储文件失败！", e);
        }
    }

    @Override
    public String hashAlgorithm() {
        return DigestAlgorithms.SHA256;
    }

    @Override
    public PrivateKey privateKey() {
        if (keyAndCert == null) {
            initKeyAndCert();
        }

        return keyAndCert.getKey();
    }

    @Override
    public Certificate[] chain() {
        if (keyAndCert == null) {
            initKeyAndCert();
        }

        return keyAndCert.getChain();
    }

    @Override
    public byte[] stamper() {
        if (stamper != null) {
            return stamper;
        }

        try {
            stamper = FileUtils.readFileToByteArray(new File(stamperPath));
        } catch (IOException e) {
            throw new RuntimeException("读取印章图片失败！", e);
        }

        return stamper;
    }

    @Override
    public float opacity() {
        return 0.8f;
    }
}
