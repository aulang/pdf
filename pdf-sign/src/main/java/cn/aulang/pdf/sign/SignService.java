package cn.aulang.pdf.sign;

import cn.aulang.pdf.sign.model.SignInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

/**
 * PDF签章服务
 *
 * @author aulang
 * @date 2021-01-15 13:21
 */
public interface SignService {
    /**
     * PDF盖章
     *
     * @param pdf      待盖章PDF输入流
     * @param outPdf   盖完章PDF输出流
     * @param signInfo 盖章信息
     * @return int 盖章数量
     * @throws IOException
     * @throws GeneralSecurityException
     */
    int sign(InputStream pdf, OutputStream outPdf, SignInfo signInfo) throws IOException, GeneralSecurityException;

    /**
     * PDF盖章
     *
     * @param pdf      待盖章PDF输入流
     * @param outPdf   盖完章PDF输出流
     * @param stamper  印章图片
     * @param signInfo 盖章信息
     * @return int 盖章数量
     * @throws IOException
     * @throws GeneralSecurityException
     */
    int sign(InputStream pdf, OutputStream outPdf, byte[] stamper, SignInfo signInfo) throws IOException, GeneralSecurityException;
}
