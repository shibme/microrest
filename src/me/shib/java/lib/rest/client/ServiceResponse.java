package me.shib.java.lib.rest.client;

public final class ServiceResponse {

    private int statusCode = 0;
    private String response;

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

}
