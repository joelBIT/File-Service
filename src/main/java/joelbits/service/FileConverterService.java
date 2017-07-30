package joelbits.service;

import joelbits.service.converter.Converter;
import joelbits.service.converter.ConverterFactory;
import joelbits.service.exception.ApiException;
import joelbits.service.file.File;
import joelbits.service.file.FileType;
import joelbits.service.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.Response.Status;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Convert a Base64 encoded file and let the client retrieve the result.
 */
@Path("/v1/type")
public class FileConverterService {
    private final static Logger log = LoggerFactory.getLogger(FileConverterService.class);

    /**
     * Converts the supplied base64 encoded data to the desired file format, if possible.
     *
     * @param file      contains the encoded data to be converted
     * @param fileType  desired file type of the converted file
     * @return          the converted data base64 encoded
     */
    @PUT
    @Path("/{fileType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response convert(File file, @PathParam("fileType") String fileType) {
        FileType originalType;
        FileType convertedType;

        try {
            originalType = FileType.fromType(file.getType());
            convertedType = FileType.fromType(fileType);
        } catch (Exception e) {
            log.error(e.toString(), e);
            return Response.status(Status.BAD_REQUEST).entity(EntityUtil.exception(Status.BAD_REQUEST, "Cannot convert " + file.getType() + " to " + fileType)).build();
        }

        try {
            Converter converter = ConverterFactory.getConverter(originalType);
            File convertFile = new File(file.getData(), convertedType.getType(), file.getName());
            byte[] result = converter.convert(convertFile);

            return Response.ok().entity(EntityUtil.encoded(result)).build();
        } catch (ApiException e) {
            log.error(e.toString(), e);
            return Response.status(e.getStatus()).entity(EntityUtil.exception(e.getStatus(), e.getMessage())).build();
        }
    }
}
