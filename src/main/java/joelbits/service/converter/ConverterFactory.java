package joelbits.service.converter;

import joelbits.service.FileType;
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
            default:
                log.warn("Converter for given type does not exist");
                throw new ApiException(Status.INTERNAL_SERVER_ERROR, "Converter for given type does not exist");
        }
    }
}
