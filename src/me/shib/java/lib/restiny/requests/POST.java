package me.shib.java.lib.restiny.requests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class POST extends Request {

    private Map<String, String> stringParameters;
    private Map<String, File> fileParameters;
    private Object requestObject;

    public POST(String methodName) {
        super(methodName, Request.RequestType.POST);
        this.stringParameters = new HashMap<>();
        this.fileParameters = new HashMap<>();
    }

    public void addParameter(String key, String value) {
        if ((key != null) && (!key.isEmpty())) {
            stringParameters.put(key, value);
        }
    }

    public void addParameter(String key, File file) {
        if ((key != null) && (!key.isEmpty())) {
            fileParameters.put(key, file);
            this.requestObject = null;
        }
    }

    public Map<String, String> getStringParameters() {
        return stringParameters;
    }

    public Map<String, File> getFileParameters() {
        return fileParameters;
    }

    public Object getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(Object postObject) {
        this.requestObject = postObject;
    }
}
