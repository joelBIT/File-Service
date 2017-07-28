package joelbits.service.converter;

import joelbits.service.File;
import joelbits.service.FileType;
import joelbits.service.exception.ApiException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.Response.Status;

import java.io.*;
import java.util.Base64;

/**
 * Convert PDF file to supplied format if supplied format is of another type.
 */
public class PDFConverter implements Converter {
    private final static Logger log = LoggerFactory.getLogger(PDFConverter.class);

    @Override
    public byte[] convert(File file) throws ApiException {
        if (FileType.PDF.getType().equalsIgnoreCase(file.getType())) {
            log.info("File has same format as desired. Conversion skipped.");
            throw new ApiException(Status.BAD_REQUEST, "Cannot convert file to the same format it already has");
        }

        String base64EncodedData = file.getData();
        byte[] fileData;

        try {
            fileData = Base64.getDecoder().decode(base64EncodedData);
        } catch (IllegalArgumentException e) {
            log.error("Data not base64 encoded", e);
            throw new ApiException(Status.BAD_REQUEST, "Data not base64 encoded");
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (Writer writer = new OutputStreamWriter(stream)) {
            PDDocument pdf = PDDocument.load(fileData);
            FileType type = FileType.valueOf(file.getType());
            switch (type) {
                case HTML:
                    new PDFDomTree().writeText(pdf, writer);
                    break;
            }

        } catch (Exception e) {
            log.error("Could not convert file", e);
            throw new ApiException(Status.INTERNAL_SERVER_ERROR, "Could not convert file");
        }

        return stream.toByteArray();
    }
}
