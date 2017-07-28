package joelbits.service;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "file")
public class File implements Serializable {
    private static final long serialVersionUID = 1L;
    private String data;
    private String type;

    public File() {}

    public File(String data, String type) {
        this.data = data;
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }
}
