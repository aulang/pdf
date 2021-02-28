package cn.aulang.pdf.sign.util;

import cn.aulang.pdf.sign.model.KeyAndCert;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

/**
 * 密钥存储帮助类
 *
 * @author aulang
 * @date 2021-01-15 14:45
 */
public class KeyStoreUtils {
    /**
     * KeyStore文件格式
     */
    public enum KeyStoreFormat {
        JKS,
        PKCS12
    }

    public static KeyAndCert extractKeyAndCert(KeyStoreFormat keyStoreFormat, InputStream keyStore, String password)
            throws Exception {
        KeyStore ks = KeyStore.getInstance(keyStoreFormat.name());

        ks.load(keyStore, password.toCharArray());

        Enumeration<String> aliases = ks.aliases();

        if (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();

            PrivateKey key = (PrivateKey) ks.getKey(alias, password.toCharArray());

            Certificate[] chain = ks.getCertificateChain(alias);

            return KeyAndCert.of(key, chain);
        }

        throw new KeyStoreException("Uninitialized keystore");
    }
}
