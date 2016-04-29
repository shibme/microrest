package me.shib.java.lib.restiny.requests;

import java.util.HashMap;
import java.util.Map;

public abstract class Request {

    private String methodName;
    private RequestType requestType;
    private Map<String, String> requestProperties;

    Request(String methodName, RequestType requestType) {
        if (methodName != null) {
            this.methodName = methodName;
        } else {
            this.methodName = "";
        }
        this.requestType = requestType;
        requestProperties = new HashMap<>();
    }

    public String getUrl(String endPoint) {
        if (endPoint == null) {
            return null;
        }
        if (!methodName.isEmpty()) {
            return endPoint + "/" + methodName;
        } else {
            return endPoint;
        }
    }

    public String getMethodName() {
        return methodName;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setProperty(String key, String value) {
        requestProperties.put(key, value);
    }

    public Map<String, String> getRequestProperties() {
        return requestProperties;
    }

    public enum RequestType {
        GET, POST
    }

}
