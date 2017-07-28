package joelbits.service;

import joelbits.service.exception.ApiException;
import joelbits.service.util.DatabaseUtil;
import joelbits.service.util.EntityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.Response.Status;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.*;

/**
 * Store and retrieve binary files. The data returned is Base64 encoded. Data sent for
 * storage must also be Base64 encoded.
 */
@Path("/v1/file")
public class FileService {
    private final static Logger log = LoggerFactory.getLogger(FileService.class);

    /**
     * Receives Base64 encoded data representing a file. Then the data is decoded to its binary
     * representation for storage in a database.
     *
     * @param file  contains the file type and encoded data
     * @return      the ID of the newly created binary file
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(File file) {
        byte[] data;
        try {
            data = decodeFileData(file);
        } catch (ApiException e) {
            return Response.status(e.getStatus()).entity(EntityUtil.exception(e.getStatus(), e.getMessage())).build();
        }

        try (Connection connection = DatabaseUtil.getConnection()) {
            Blob blob = connection.createBlob();
            blob.setBytes(1, data);

            PreparedStatement statement = connection.prepareStatement("insert into FILES values(default, ?, ?, ?, null)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, file.getType().toUpperCase());
            statement.setBlob(2, blob);
            Timestamp timestamp = Timestamp.from(Calendar.getInstance().toInstant());
            statement.setTimestamp(3, timestamp);
            statement.execute();

            String fileId = extractGeneratedID(statement);

            return Response.ok().entity(createEntity("id", fileId)).build();
        } catch (Exception e) {
            log.error(e.toString(), e);
            return Response.serverError().entity(EntityUtil.exception(Status.INTERNAL_SERVER_ERROR, "Could not insert data into database")).build();
        }
    }

    private byte[] decodeFileData(File file) throws ApiException {
        if (StringUtils.isEmpty(file.getData())) {
            throw new ApiException(Status.BAD_REQUEST, "Data property is empty");
        }

        String base64EncodedData = file.getData();
        try {
            return Base64.getDecoder().decode(base64EncodedData);
        } catch (IllegalArgumentException e) {
            throw new ApiException(Status.BAD_REQUEST, "Data not base64 encoded");
        }
    }

    private Map<String, Object> createEntity(String key, String value) {
        Map<String, Object> entity = new HashMap<>();
        entity.put(key, value);

        return entity;
    }

    private String extractGeneratedID(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.getGeneratedKeys();
        resultSet.next();

        return resultSet.getString(1);
    }

    /**
     * Retrieves the binary data matching the supplied file ID and Base64 encodes it before
     * returning the encoded data to the client.
     *
     * @param fileid    ID of the file to be retrieved from a database
     * @return          the binary data Base64 encoded
     */
    @GET
    @Path("/{fileId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieve(@PathParam("fileId") String fileid) {
        int fileId;
        try {
            fileId = verifyId(fileid);
        } catch (ApiException e) {
            return Response.status(e.getStatus()).entity(EntityUtil.exception(e.getStatus(), e.getMessage())).build();
        }

        try (Connection connection = DatabaseUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM FILES WHERE ID = ?");
            statement.setInt(1, fileId);
            ResultSet resultSet = statement.executeQuery();

            byte[] fileData = extractBinaryData(resultSet);

            return Response.ok().entity(EntityUtil.encoded(fileData)).build();
        } catch (ApiException e) {
            log.error(e.toString(), e);
            return Response.status(Status.NOT_FOUND).entity(EntityUtil.exception(e.getStatus(), e.getMessage())).build();
        } catch (Exception e) {
            log.error(e.toString(), e);
            return Response.serverError().entity(EntityUtil.exception(Status.INTERNAL_SERVER_ERROR, "Could not retrieve data from database")).build();
        }
    }

    private int verifyId(String id) throws ApiException {
        if (StringUtils.isEmpty(id)) {
            log.error("The ID property is missing");
            throw new ApiException(Status.BAD_REQUEST, "The ID property is missing");
        }

        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            log.error(e.toString(), e);
            throw new ApiException(Status.BAD_REQUEST, "The ID property value is not an integer");
        }
    }

    private byte[] extractBinaryData(ResultSet resultSet) throws ApiException, SQLException {
        if (!resultSet.next()) {
            throw new ApiException(Status.NOT_FOUND, "No file found");
        }
        Blob blob = resultSet.getBlob("DATA");

        return blob.getBytes(1, (int) blob.length());
    }
}