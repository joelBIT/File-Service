package joelbits.service.converter;

import joelbits.service.file.File;
import joelbits.service.file.FileType;
import joelbits.service.exception.ApiException;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.fit.pdfdom.PDFDomTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.Response.Status;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

/**
 * Convert PDF file to supplied format if possible.
 */
public class PDFConverter implements Converter {
    private final static Logger log = LoggerFactory.getLogger(PDFConverter.class);

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
                case HTML:
                    new PDFDomTree().writeText(PDDocument.load(fileData), new OutputStreamWriter(outputStream));
                    return outputStream.toByteArray();
                case TXT:
                    return toText(fileData, outputStream);
                case JPEG:
                case JPG:
                case PNG:
                case BMP:
                case GIF:
                    return toImage(fileData, type.getType(), outputStream);
                default:
                    log.warn("Could not find matching conversion for type " + type);
                    return new byte[0];
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ApiException(Status.INTERNAL_SERVER_ERROR, "Could not convert file " + file.getName());
        }
    }

    private byte[] toText(byte[] fileData, ByteArrayOutputStream outputStream) throws IOException {
        PDFParser parser = new PDFParser(new RandomAccessBuffer(fileData));
        parser.parse();

        COSDocument document = parser.getDocument();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        PDDocument pdf = new PDDocument(document);
        String parsedText = pdfStripper.getText(pdf);

        PrintWriter writer = new PrintWriter(outputStream);
        writer.print(parsedText);
        writer.close();
        pdf.close();
        document.close();

        return outputStream.toByteArray();
    }

    private byte[] toImage(byte[] fileData, String format, ByteArrayOutputStream outputStream) throws IOException {
        PDDocument pdf = PDDocument.load(fileData);
        PDFRenderer pdfRenderer = new PDFRenderer(pdf);
        for (int page = 0; page < pdf.getNumberOfPages(); ++page) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
            ImageIOUtil.writeImage(bim, format, outputStream, 300);
        }
        pdf.close();

        return outputStream.toByteArray();
    }
}
