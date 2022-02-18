package ssp.smartstudentportal.Structures;

import java.io.Serializable;

public class ClientResponse implements Serializable {
    private final Response type;
    private final Object data;

    public ClientResponse(Response type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Response getResponseType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
