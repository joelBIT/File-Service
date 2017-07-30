package joelbits.service.converter;

import joelbits.service.file.File;
import joelbits.service.exception.ApiException;

public interface Converter {
    byte[] convert(File file) throws ApiException;
}
