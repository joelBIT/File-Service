package joelbits.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum FileType {
    PDF("PDF"),
    HTML("HTML"),
    JPG("JPG"),
    JPEG("JPEG"),
    GIF("GIF"),
    TIFF("TIFF"),
    PNG("PNG"),
    TXT("TXT");

    private final String type;

    FileType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static List<String> imageTypes() {
        return toTypes(Arrays.asList(JPG, JPEG, GIF, TIFF, PNG));
    }

    private static List<String> toTypes(List<FileType> types) {
        List<String> result = new ArrayList<String>();
        for (FileType type : types) {
            result.add(type.getType());
        }
        return result;
    }
}
