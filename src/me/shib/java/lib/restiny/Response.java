package me.shib.java.lib.restiny;

import me.shib.java.lib.utils.JsonUtil;

import java.util.List;
import java.util.Map;

public final class Response {

    private int statusCode = 0;
    private String response;
    private JsonUtil jsonUtil;
    private Map<String, List<String>> headerFields;

    protected Response(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    public int getStatusCode() {
        return statusCode;
    }

    protected void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponse() {
        return response;
    }

    protected void setResponse(String response) {
        this.response = response;
    }

    public Map<String, List<String>> getHeaderFields() {
        return headerFields;
    }

    protected void setHeaderFields(Map<String, List<String>> headerFields) {
        this.headerFields = headerFields;
    }

    public <T> T getResponse(Class<T> classOfT) {
        if (response != null) {
            try {
                return jsonUtil.fromJson(response, classOfT);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

}
