package joelbits.service.converter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import joelbits.service.file.File;
import joelbits.service.file.FileType;
import joelbits.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Base64;

import static javax.ws.rs.core.Response.Status;

/**
 * Convert HTML file to supplied format if possible.
 */
public class HTMLConverter implements Converter {
    private final static Logger log = LoggerFactory.getLogger(HTMLConverter.class);

    @Override
    public byte[] convert(File file) throws ApiException {
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
                    return convertToPDF(fileData, outputStream);
                default:
                    log.warn("Could not find matching conversion for type " + type);
                    return new byte[0];
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ApiException(Status.INTERNAL_SERVER_ERROR, "Could not convert file " + file.getName());
        }
    }

    private byte[] convertToPDF(byte[] fileData, ByteArrayOutputStream outputStream) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(fileData));
        document.close();

        return outputStream.toByteArray();
    }
}
