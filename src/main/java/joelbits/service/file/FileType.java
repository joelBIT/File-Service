package joelbits.service.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum FileType {
    PDF("PDF"),
    HTML("HTML"),
    JPG("JPG"),
    JPEG("JPEG"),
    GIF("GIF"),
    BMP("BMP"),
    PNG("PNG"),
    TXT("TXT");

    private static final Logger log = LoggerFactory.getLogger(FileType.class);
    private final String type;

    FileType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static FileType fromType(String type) {
        for (FileType fileType : values()) {
            if (fileType.getType().equalsIgnoreCase(type)) {
                return fileType;
            }
        }
        log.warn("Could not find any match for file type " + type);
        throw new IllegalArgumentException("The file type " + type + " is unknown");
    }

    public static List<FileType> imageTypes() {
        return Arrays.asList(JPG, JPEG, GIF, BMP, PNG);
    }

    private static List<String> toTypes(List<FileType> types) {
        List<String> result = new ArrayList<String>();
        for (FileType type : types) {
            result.add(type.getType());
        }
        return result;
    }
}
