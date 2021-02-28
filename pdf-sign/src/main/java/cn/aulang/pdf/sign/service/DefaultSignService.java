package cn.aulang.pdf.sign.service;

import cn.aulang.pdf.sign.SignService;
import cn.aulang.pdf.sign.StamperService;
import cn.aulang.pdf.sign.core.ExPdfSigner;
import cn.aulang.pdf.sign.model.SignInfo;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Security;

/**
 * @author aulang
 * @date 2021-01-15 13:34
 */
@Slf4j
public class DefaultSignService implements SignService {
    private final StamperService stamperService;

    public DefaultSignService(StamperService stamperService) {
        this.stamperService = stamperService;
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public int sign(InputStream pdf, OutputStream outPdf, SignInfo signInfo)
            throws IOException, GeneralSecurityException {
        ImageData stamper = ImageDataFactory.create(stamperService.stamper());
        return sign(pdf, outPdf, stamper, signInfo);
    }

    @Override
    public int sign(InputStream pdf, OutputStream outPdf, byte[] stamper, SignInfo signInfo)
            throws IOException, GeneralSecurityException {
        ImageData imageData = ImageDataFactory.create(stamper);
        return sign(pdf, outPdf, imageData, signInfo);
    }

    public int sign(InputStream pdf, OutputStream outPdf, ImageData stamper, SignInfo signInfo)
            throws IOException, GeneralSecurityException {
        try (PdfReader reader = new PdfReader(pdf)) {
            StampingProperties properties = new StampingProperties().useAppendMode();

            ExPdfSigner signer = new ExPdfSigner(reader, outPdf, signInfo, stamper, properties)
                    .setOpacity(stamperService.opacity());

            IExternalDigest digest = new BouncyCastleDigest();

            PrivateKeySignature pks = new PrivateKeySignature(
                    stamperService.privateKey(),
                    stamperService.hashAlgorithm(),
                    BouncyCastleProvider.PROVIDER_NAME
            );

            return signer.sign(
                    digest,
                    pks,
                    stamperService.chain(),
                    null,
                    null,
                    null,
                    0,
                    PdfSigner.CryptoStandard.CMS);
        }
    }
}
