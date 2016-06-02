package me.shib.java.lib.restiny;

import me.shib.java.lib.restiny.util.JsonUtil;

public final class Response {

    private int statusCode = 0;
    private String response;
    private JsonUtil jsonUtil;

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
