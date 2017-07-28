package joelbits.service;

import joelbits.service.converter.Converter;
import joelbits.service.converter.ConverterFactory;
import joelbits.service.exception.ApiException;
import joelbits.service.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.Response.Status;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * For converting existing files in database and store the converted file as well as keeping the old file
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
        FileType type;
        FileType paramType;

        try {
            type = FileType.valueOf(file.getType().toUpperCase());
            paramType = FileType.valueOf(fileType.toUpperCase());
        } catch (Exception e) {
            log.error(e.toString(), e);
            return Response.status(Status.BAD_REQUEST).entity(EntityUtil.exception(Status.BAD_REQUEST, "Cannot convert " + file.getType() + " to " + fileType)).build();
        }

        try {
            Converter converter = ConverterFactory.getConverter(type);
            File convertFile = new File(file.getData(), paramType.getType());
            byte[] result = converter.convert(convertFile);

            return Response.ok().entity(EntityUtil.encoded(result)).build();
        } catch (ApiException e) {
            log.error(e.toString(), e);
            return Response.status(e.getStatus()).entity(EntityUtil.exception(e.getStatus(), e.getMessage())).build();
        }
    }
}
