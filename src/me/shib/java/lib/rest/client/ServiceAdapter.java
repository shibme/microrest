package me.shib.java.lib.rest.client;

import java.io.IOException;
import java.util.ArrayList;

public final class ServiceAdapter {

    private HTTPServiceClient httpClinetHandler;
    private String endPoint;

    public ServiceAdapter(String endPoint) {
        this.endPoint = endPoint;
        httpClinetHandler = new HTTPServiceClient();
    }

    private ServiceResponse get(String apiName, Object requestObjects, ArrayList<Parameter> params) throws IOException {
        if (apiName == null) {
            apiName = "";
        }
        StringBuilder requestUrlBuilder = new StringBuilder();
        requestUrlBuilder.append(endPoint);
        if (!apiName.isEmpty()) {
            requestUrlBuilder.append("/").append(apiName);
        }
        return httpClinetHandler.get(requestUrlBuilder.toString(), requestObjects, params);
    }

    public ServiceResponse get(String apiName, Object requestObject) throws IOException {
        return get(apiName, requestObject, null);
    }

    public ServiceResponse get(String apiName, ArrayList<Parameter> params) throws IOException {
        return get(apiName, null, params);
    }

    public ServiceResponse get(String apiName) throws IOException {
        return get(apiName, null, null);
    }

    public ServiceResponse post(String apiName, Object requestObjects, ArrayList<Parameter> params)
            throws IOException {
        if (apiName == null) {
            apiName = "";
        }
        StringBuilder requestUrlBuilder = new StringBuilder();
        requestUrlBuilder.append(endPoint);
        if (!apiName.isEmpty()) {
            requestUrlBuilder.append("/").append(apiName);
        }
        return httpClinetHandler.post(requestUrlBuilder.toString(), requestObjects, params);
    }

    public ServiceResponse post(String apiName, Object requestObject) throws IOException {
        return post(apiName, requestObject, null);
    }

    public ServiceResponse post(String apiName, ArrayList<Parameter> params) throws IOException {
        return post(apiName, null, params);
    }

    public ServiceResponse post(String apiName) throws IOException {
        return post(apiName, null, null);
    }

}
