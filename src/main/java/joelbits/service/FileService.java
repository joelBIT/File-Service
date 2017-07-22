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

@Path("/v1/pdf")
public class FileService {
    private final static Logger log = LoggerFactory.getLogger(FileService.class);

    /**
     * Creates an empty PDF document containing the supplied metadata.
     *
     * @param metadata      Metadata for the PDF document to be created
     * @return              Empty base64-encoded PDF document
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(MetaData metadata) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try(PDDocument document = createDocument(metadata)) {
            document.save(stream);
        } catch(IOException e) {
            log.error(e.toString(), e);
            return Response.serverError().entity("Error saving document").build();
        }

        return Response.ok().entity(createEncodedEntity(stream.toByteArray())).build();
    }

    private PDDocument createDocument(MetaData metadata) {
        PDDocument document = new PDDocument();
        addPages(metadata, document);
        addMetaData(metadata, document);

        return document;
    }

    private void addPages(MetaData metadata, PDDocument document) {
        for (int i = 0; i < metadata.getNumberOfPages(); i++) {
            document.addPage(new PDPage());
        }

        // A document should always have at least 1 page
        if (document.getPages().getCount() == 0) {
            document.addPage(new PDPage());
        }
    }

    private void addMetaData(MetaData metadata, PDDocument document) {
        PDDocumentInformation information = document.getDocumentInformation();

        information.setAuthor(metadata.getAuthor());
        information.setTitle(metadata.getTitle());
        information.setCreator(metadata.getCreator());
        information.setSubject(metadata.getSubject());
        information.setKeywords(StringUtils.join(metadata.getKeyWords(), ", "));

        information.setCreationDate(Calendar.getInstance());
        information.setModificationDate(Calendar.getInstance());
    }

    private Map<String, Object> createEncodedEntity(byte[] input) {
        String base64EncodedByteArray = Base64.getEncoder().encodeToString(input);
        return createEntity(base64EncodedByteArray);
    }

    private Map<String, Object> createEntity(String input) {
        Map<String, Object> entity = new HashMap<>();
        entity.put("data", input);

        return entity;
    }
}