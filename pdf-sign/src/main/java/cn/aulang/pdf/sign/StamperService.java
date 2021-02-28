package cn.aulang.pdf.sign;

import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * 印章服务
 *
 * @author Aulang
 * @date 2021-01-16 21:18
 */
public interface StamperService {
    /**
     * 摘要算法
     *
     * @return 摘要算法
     */
    String hashAlgorithm();

    /**
     * 私钥
     *
     * @return 印章私钥
     */
    PrivateKey privateKey();

    /**
     * 证书链
     *
     * @return 印章证书链
     */
    Certificate[] chain();

    /**
     * 印章图片
     *
     * @return 印章图片
     */
    byte[] stamper();

    /**
     * 印章图片透明度，防止遮挡正文
     *
     * @return float
     */
    float opacity();
}
