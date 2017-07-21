package joelbits.service;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "metadata")
public class MetaData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String author;
    private String title;
    private String subject;
    private String creator;
    private List<String> keyWords;

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getCreator() {
        return creator;
    }

    public List<String> getKeyWords() {
        return keyWords;
    }
}
