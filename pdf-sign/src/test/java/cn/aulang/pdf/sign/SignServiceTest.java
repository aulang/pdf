package cn.aulang.pdf.sign;

import cn.aulang.pdf.sign.model.SignInfo;
import cn.aulang.pdf.sign.service.DefaultSignService;
import cn.aulang.pdf.sign.service.DefaultStamperService;
import cn.aulang.pdf.sign.util.KeyStoreUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

/**
 * @author Aulang
 * @date 2021-01-17 11:21
 */
public class SignServiceTest {

    public static void main(String[] args) throws Exception {
        StamperService stamperService = new DefaultStamperService(
                KeyStoreUtils.KeyStoreFormat.PKCS12,
                "D:/Document/aulang.p12",
                "Aulang88",
                "D:/Document/logo.png"
        );

        SignService service = new DefaultSignService(stamperService);

        try (
                InputStream pdf = new FileInputStream("D:/Document/测试文档.pdf");
                OutputStream signedPdf = new FileOutputStream("D:/Document/测试文档_signed.pdf")
        ) {
            SignInfo signInfo = SignInfo.builder()
                    .reason("测试")
                    .signDate(System.currentTimeMillis())
                    .location("武汉")
                    .creator("Aulang")
                    .contact("Aulang")
                    .regex("间距")
                    .pages(Collections.singletonList(1))
                    .latest(true)
                    .build();

            service.sign(pdf, signedPdf, signInfo);
        }
    }
}