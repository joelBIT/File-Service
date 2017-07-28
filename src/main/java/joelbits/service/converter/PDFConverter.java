package joelbits.service.converter;

import joelbits.service.File;
import joelbits.service.FileType;
import joelbits.service.exception.ApiException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.fit.pdfdom.PDFDomTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.Response.Status;

import java.awt.image.BufferedImage;
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

        byte[] fileData;
        try {
            fileData = Base64.getDecoder().decode(file.getData());
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
                case JPG:
                    toImage(pdf, type.getType(), stream);
                    break;
            }
        } catch (Exception e) {
            log.error("Could not convert file", e);
            throw new ApiException(Status.INTERNAL_SERVER_ERROR, "Could not convert file");
        }

        return stream.toByteArray();
    }

    private void toImage(PDDocument pdf, String format, OutputStream outputStream) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(pdf);
        for (int page = 0; page < pdf.getNumberOfPages(); ++page) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
            ImageIOUtil.writeImage(bim, format, outputStream, 300);
        }
    }
}
