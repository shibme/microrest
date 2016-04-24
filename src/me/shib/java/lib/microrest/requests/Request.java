package me.shib.java.lib.microrest.requests;

import java.util.HashMap;
import java.util.Map;

public abstract class Request {

    private String methodName;
    private RequestType requestType;
    private Map<String, String> requestProperties;
    private String url;

    protected Request(String methodName, RequestType requestType) {
        if (methodName != null) {
            this.methodName = methodName;
        } else {
            this.methodName = "";
        }
        this.requestType = requestType;
        requestProperties = new HashMap<>();
    }

    public void setEndpoint(String endPoint) {
        if (!methodName.isEmpty()) {
            this.url = endPoint + "/" + methodName;
        } else {
            this.url = endPoint;
        }
    }

    public String getUrl() {
        return url;
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
