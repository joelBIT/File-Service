package joelbits.service.converter;

import joelbits.service.File;
import joelbits.service.exception.ApiException;

public interface Converter {
    byte[] convert(File file) throws ApiException;
}
