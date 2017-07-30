package joelbits.service.converter;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import joelbits.service.exception.ApiException;
import joelbits.service.file.File;
import joelbits.service.file.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.Response.Status;

import java.io.*;
import java.util.Base64;

/**
 * Convert TXT file to supplied format if possible.
 */
public class TXTConverter implements Converter {
    private final static Logger log = LoggerFactory.getLogger(TXTConverter.class);

    @Override
    public byte[] convert(File file) throws ApiException {
        if (FileType.TXT.equals(FileType.fromType(file.getType()))) {
            log.info("File has same format as desired. Conversion skipped.");
            throw new ApiException(Status.BAD_REQUEST, "Cannot convert file to the same format it already has");
        }

        byte[] fileData;
        try {
            fileData = Base64.getDecoder().decode(file.getData());
        } catch (IllegalArgumentException e) {
            log.error(e.toString(), e);
            throw new ApiException(Status.BAD_REQUEST, "Data not base64 encoded");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            FileType type = FileType.fromType(file.getType());
            switch (type) {
                case PDF:
                    return toPDF(fileData, outputStream);
                default:
                    log.warn("Could not find matching conversion for type " + type);
                    return new byte[0];
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ApiException(Status.INTERNAL_SERVER_ERROR, "Could not convert file " + file.getName());
        }
    }

    private byte[] toPDF(byte[] fileData, ByteArrayOutputStream outputStream) throws IOException, DocumentException {
        Document pdf = new Document(PageSize.A4);
        PdfWriter.getInstance(pdf, outputStream).setPdfVersion(PdfWriter.PDF_VERSION_1_7);
        pdf.open();

        Font font = createFont();
        pdf.add(new Paragraph("\n"));
        addTextToDocument(fileData, pdf, font);
        pdf.close();

        return outputStream.toByteArray();
    }

    private Font createFont() throws DocumentException, IOException {
        BaseFont courier = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED);
        Font font = new Font(courier);
        font.setStyle(Font.NORMAL);
        font.setSize(11);

        return font;
    }

    private void addTextToDocument(byte[] fileData, Document pdf, Font font) throws IOException, DocumentException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileData)));
        String strLine;
        while ((strLine = reader.readLine()) != null) {
            Paragraph paragraph = new Paragraph(strLine + "\n", font);
            paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
            pdf.add(paragraph);
        }
        reader.close();
    }
}
