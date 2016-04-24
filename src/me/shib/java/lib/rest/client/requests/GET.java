package me.shib.java.lib.rest.client.requests;

import java.util.HashMap;
import java.util.Map;

public final class GET extends Request {

    private Map<String, String> parameters;

    public GET(String methodName) {
        super(methodName, RequestType.GET);
        this.parameters = new HashMap<>();
    }

    public void addParameter(String key, String value) {
        if ((key != null) && (!key.isEmpty())) {
            parameters.put(key, value);
        }
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
