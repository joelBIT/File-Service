package joelbits.service.file;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "file")
public class File implements Serializable {
    private static final long serialVersionUID = 1L;
    private String data;
    private String type;
    private String name;

    public File() {}

    public File(String data, String type, String name) {
        this.data = data;
        this.type = type;
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public String getName() { return name; }
}
