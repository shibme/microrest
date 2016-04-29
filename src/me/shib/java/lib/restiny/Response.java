package me.shib.java.lib.restiny;

import me.shib.java.lib.common.utils.JsonLib;

public final class Response {

    private int statusCode = 0;
    private String response;
    private JsonLib jsonLib;

    Response(JsonLib jsonLib) {
        this.jsonLib = jsonLib;
    }

    public int getStatusCode() {
        return statusCode;
    }

    void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponse() {
        return response;
    }

    void setResponse(String response) {
        this.response = response;
    }

    public <T> T getResponse(Class<T> classOfT) {
        if (response != null) {
            try {
                return jsonLib.fromJson(response, classOfT);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

}
