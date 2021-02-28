package cn.aulang.pdf.sign.util;

import cn.aulang.pdf.sign.model.SignInfo;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.parser.PdfDocumentContentParser;
import com.itextpdf.kernel.pdf.canvas.parser.listener.DefaultPdfTextLocation;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.kernel.pdf.canvas.parser.listener.RegexBasedLocationExtractionStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * PDF正则表达式位置提取帮助类
 *
 * @author Aulang
 * @date 2021-01-16 19:43
 */
public class RegexLocationUtils {

    public static List<IPdfTextLocation> extractLocation(PdfDocument pdfDocument, SignInfo signInfo) {
        List<IPdfTextLocation> locations = new ArrayList<>();

        RegexBasedLocationExtractionStrategy strategy = new RegexBasedLocationExtractionStrategy(signInfo.getRegex());

        PdfDocumentContentParser parser = new PdfDocumentContentParser(pdfDocument);

        int totalPages = pdfDocument.getNumberOfPages();

        for (Integer page : signInfo.getPages()) {
            if (page < 0) {
                page = totalPages + page + 1;
            }

            parser.processContent(page, strategy);

            Collection<IPdfTextLocation> collection = strategy.getResultantLocations();

            for (IPdfTextLocation e : collection) {
                ((DefaultPdfTextLocation) e).setPageNr(page);
            }

            locations.addAll(collection);
        }

        return locations;
    }
}
