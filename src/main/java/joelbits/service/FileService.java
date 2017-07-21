package joelbits.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.*;

@Path("/v1")
public class FileService {
    private final static Logger log = LoggerFactory.getLogger(FileService.class);

    /**
     * Creates an empty PDF document containing the supplied metadata.
     *
     * @param metadata      Metadata for the PDF document to be created
     * @return              Empty base64-encoded PDF document
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(MetaData metadata) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        addMetaData(metadata, document);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            document.save(byteArrayOutputStream);
            document.close();
        } catch (IOException e) {
            log.error(e.toString(), e);
            return Response.serverError().entity("Error saving document").build();
        }

        String base64EncodedByteArray = encodeDocument(byteArrayOutputStream);
        Map<String, Object> documentEntity = createEntity(base64EncodedByteArray);

        return Response.ok().entity(documentEntity).build();
    }

    private void addMetaData(MetaData metadata, PDDocument document) {
        PDDocumentInformation information = document.getDocumentInformation();
        information.setAuthor(metadata.getAuthor());
        information.setTitle(metadata.getTitle());
        information.setCreator(metadata.getCreator());
        information.setSubject(metadata.getSubject());
        information.setCreationDate(Calendar.getInstance());
        information.setModificationDate(Calendar.getInstance());
        information.setKeywords(StringUtils.join(metadata.getKeyWords(), ", "));
    }

    private Map<String, Object> createEntity(String base64EncodedByteArray) {
        Map<String, Object> documentEntity = new HashMap<>();
        documentEntity.put("data", base64EncodedByteArray);
        return documentEntity;
    }

    private String encodeDocument(ByteArrayOutputStream byteArrayOutputStream) {
        byte[] file = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(file);
    }
}