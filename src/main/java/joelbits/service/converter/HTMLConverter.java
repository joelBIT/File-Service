package joelbits.service.converter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import joelbits.service.File;
import joelbits.service.FileType;
import joelbits.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Base64;

import static javax.ws.rs.core.Response.Status;

public class HTMLConverter implements Converter {
    private final static Logger log = LoggerFactory.getLogger(HTMLConverter.class);

    @Override
    public byte[] convert(File file) throws ApiException {
        if (FileType.HTML.getType().equalsIgnoreCase(file.getType())) {
            log.info("File has same format as desired. Conversion skipped.");
            throw new ApiException(Status.BAD_REQUEST, "Cannot convert file to the same format it already has");
        }

        byte[] fileData;
        try {
            fileData = Base64.getDecoder().decode(file.getData());
        } catch (IllegalArgumentException e) {
            log.error("Data not base64 encoded", e);
            throw new ApiException(Status.BAD_REQUEST, "Data not base64 encoded");
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            FileType type = FileType.valueOf(file.getType());
            switch (type) {
                case PDF:
                    return convertToPDF(fileData, stream);
            }
        } catch (Exception e) {
            log.error("Could not convert file", e);
            throw new ApiException(Status.INTERNAL_SERVER_ERROR, "Could not convert file");
        }

        return stream.toByteArray();
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
