package joelbits.service.converter;

import joelbits.service.file.FileType;
import joelbits.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.Response.Status;

public class ConverterFactory {
    private final static Logger log = LoggerFactory.getLogger(ConverterFactory.class);

    public static Converter getConverter(FileType type) throws ApiException {
        switch(type) {
            case PDF:
                return new PDFConverter();
            case HTML:
                return new HTMLConverter();
            case TXT:
                return new TXTConverter();
            case JPEG:
            case JPG:
            case PNG:
            case BMP:
            case GIF:
                return new ImageConverter();
            default:
                log.warn("Converter does not exist for given type");
                throw new ApiException(Status.INTERNAL_SERVER_ERROR, "Converter does not exist for given type");
        }
    }
}
