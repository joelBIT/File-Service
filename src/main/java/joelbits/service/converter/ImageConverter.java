package joelbits.service.converter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
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
 * Convert image file to supplied format if possible.
 */
public class ImageConverter implements Converter {
    private final static Logger log = LoggerFactory.getLogger(ImageConverter.class);

    @Override
    public byte[] convert(File file) throws ApiException {
        if (FileType.imageTypes().contains(FileType.fromType(file.getType()))) {
            log.info("File does not have an allowed image format. Conversion skipped.");
            throw new ApiException(Status.BAD_REQUEST, "File does not have an allowed image format.");
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
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        writer.open();
        document.open();
        document.add(Image.getInstance(fileData));
        document.close();
        writer.close();

        return outputStream.toByteArray();
    }
}
