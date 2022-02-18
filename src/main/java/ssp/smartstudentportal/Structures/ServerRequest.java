package ssp.smartstudentportal.Structures;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerRequest implements Serializable {
    private final Request type;
    private final ArrayList<Object> params;

    public ServerRequest(Request type, ArrayList<Object> params) {
        this.type = type;
        this.params = params;
    }

    public Request getRequestType() {
        return type;
    }

    public ArrayList<Object> getParams() {
        return params;
    }
}
