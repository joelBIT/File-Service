package joelbits.service.util;

import static javax.ws.rs.core.Response.Status;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class EntityUtil {

    public static Map<String, Object> exception(Status status, String message) {
        Map<String, Object> apiException = new HashMap<>();
        apiException.put("status", status.getStatusCode());
        apiException.put("message", message);

        return apiException;
    }

    public static Map<String, Object> encoded(byte[] input) {
        String base64EncodedByteArray = Base64.getEncoder().encodeToString(input);

        return createEntity("data", base64EncodedByteArray);
    }

    private static Map<String, Object> createEntity(String key, String value) {
        Map<String, Object> entity = new HashMap<>();
        entity.put(key, value);

        return entity;
    }
}
