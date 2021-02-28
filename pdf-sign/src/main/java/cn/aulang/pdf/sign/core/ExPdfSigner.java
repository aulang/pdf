package cn.aulang.pdf.sign.core;

import cn.aulang.pdf.sign.model.SignInfo;
import cn.aulang.pdf.sign.util.RegexLocationUtils;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.PdfSigner;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import static com.itextpdf.signatures.PdfSignatureAppearance.RenderingMode.GRAPHIC;

/**
 * @author Aulang
 * @date 2021-01-16 12:57
 */
@Slf4j
public class ExPdfSigner extends PdfSigner {
    public static final float MARGIN = 2;

    private float opacity = 1.0f;

    private final SignInfo signInfo;
    private final ImageData stamper;

    public ExPdfSigner(PdfReader reader,
                       OutputStream outputStream,
                       SignInfo signInfo,
                       ImageData stamper,
                       StampingProperties properties)
            throws IOException {
        super(reader, outputStream, properties);

        this.signInfo = signInfo;
        this.stamper = stamper;
    }

    public ExPdfSigner setOpacity(float opacity) {
        this.opacity = opacity;
        return this;
    }

    public int sign(IExternalDigest externalDigest,
                    IExternalSignature externalSignature,
                    Certificate[] chain,
                    Collection<ICrlClient> crlList,
                    IOcspClient ocspClient,
                    ITSAClient tsaClient,
                    int estimatedSize,
                    CryptoStandard sigType)
            throws IOException, GeneralSecurityException {
        List<IPdfTextLocation> locations = RegexLocationUtils.extractLocation(document, signInfo);

        if (locations.isEmpty()) {
            log.warn("没有找到要求的盖章位置：{}", signInfo);
            return 0;
        }

        Calendar calendar = Calendar.getInstance();
        if (signInfo.getSignDate() != null) {
            calendar.setTimeInMillis(signInfo.getSignDate());
        }
        setSignDate(calendar);

        appearance
                .setReason(signInfo.getReason())
                .setLocation(signInfo.getLocation())
                .setSignatureCreator(signInfo.getCreator())
                .setContact(signInfo.getContact())
                .setSignatureGraphic(stamper)
                .setRenderingMode(GRAPHIC);

        int index = locations.size() - 1;
        if (signInfo.getLatest() != null && !signInfo.getLatest()) {
            index = 0;
        }
        IPdfTextLocation location = locations.get(index);

        Rectangle locationRectangle = location.getRectangle();
        float locationX = locationRectangle.getX();
        float locationY = locationRectangle.getY();
        float locationWidth = locationRectangle.getWidth();
        float locationHeight = locationRectangle.getHeight();

        float imgWidth = stamper.getWidth();
        float imgHeight = stamper.getHeight();


        float x = locationX + (locationWidth - imgWidth) / 2;
        float y = locationY + (locationHeight - imgHeight) / 2;

        Rectangle rectangle = new Rectangle(x, y, imgWidth, imgHeight);
        appearance.setPageRect(rectangle);
        initAppearanceLayer2();

        super.signDetached(externalDigest,
                externalSignature,
                chain,
                crlList,
                ocspClient,
                tsaClient,
                estimatedSize,
                sigType,
                (SignaturePolicyIdentifier) null);

        return 1;
    }

    protected void initAppearanceLayer2() {
        PdfFormXObject n2 = appearance.getLayer2();
        PdfCanvas canvas = new PdfCanvas(n2, document);

        int page = appearance.getPageNumber();
        Rectangle rect = appearance.getPageRect();
        int rotation = document.getPage(page).getRotation();

        if (rotation == 90) {
            canvas.concatMatrix(0, 1, -1, 0, rect.getWidth(), 0);
        } else if (rotation == 180) {
            canvas.concatMatrix(-1, 0, 0, -1, rect.getWidth(), rect.getHeight());
        } else if (rotation == 270) {
            canvas.concatMatrix(0, -1, 1, 0, 0, rect.getHeight());
        }

        Rectangle rotatedRect = rotateRectangle(rect, document.getPage(page).getRotation());

        Rectangle signatureRect = new Rectangle(
                MARGIN,
                MARGIN,
                rotatedRect.getWidth() - 2 * MARGIN,
                rotatedRect.getHeight() - 2 * MARGIN);

        ImageData signatureGraphic = appearance.getSignatureGraphic();
        float imgWidth = signatureGraphic.getWidth();

        if (imgWidth == 0) {
            imgWidth = signatureRect.getWidth();
        }

        float imgHeight = signatureGraphic.getHeight();

        if (imgHeight == 0) {
            imgHeight = signatureRect.getHeight();
        }

        float multiplierH = signatureRect.getWidth() / signatureGraphic.getWidth();
        float multiplierW = signatureRect.getHeight() / signatureGraphic.getHeight();
        float multiplier = Math.min(multiplierH, multiplierW);
        imgWidth *= multiplier;
        imgHeight *= multiplier;

        float x = signatureRect.getLeft() + (signatureRect.getWidth() - imgWidth) / 2;
        float y = signatureRect.getBottom() + (signatureRect.getHeight() - imgHeight) / 2;

        PdfExtGState gState = new PdfExtGState();
        gState.setFillOpacity(opacity);
        gState.setStrokeOpacity(opacity);
        canvas.setExtGState(gState);

        canvas.addImageWithTransformationMatrix(stamper, imgWidth, 0, 0, imgHeight, x, y);
    }

    public static Rectangle rotateRectangle(Rectangle rect, int angle) {
        if (0 == (angle / 90) % 2) {
            return new Rectangle(rect.getWidth(), rect.getHeight());
        } else {
            return new Rectangle(rect.getHeight(), rect.getWidth());
        }
    }
}
