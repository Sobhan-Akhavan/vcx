package ir.vcx.exception.runtime;

import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

public class JsonProcessException extends VCXRuntimeException {

    private String message;


    public JsonProcessException(String message) {
        this.message = message;
    }

    public JsonProcessException(Exception e, String simpleClassName) {
        String eMessage = e.getMessage();
        if (e instanceof JsonMappingException && eMessage.startsWith("Can not deserialize instance")) {
            message = "Invalid json format - ";
            int index = eMessage.indexOf("line:");
            message += eMessage.substring(index, eMessage.indexOf("]", index));
            message += " - " + simpleClassName;

        } else {
            message = "Invalid json format";
        }
        super.initCause(e);
    }

    public JsonProcessException(Exception e) {
        message = "Invalid json format";
        super.initCause(e);
    }

    public String getMessage() {
        return this.message;
    }
}
