package me.shib.java.lib.microrest.requests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class POST extends Request {

    private Map<String, String> stringParameters;
    private Map<String, File> fileParameters;
    private Object postObject;

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
            this.postObject = null;
        }
    }

    public Map<String, String> getStringParameters() {
        return stringParameters;
    }

    public Map<String, File> getFileParameters() {
        return fileParameters;
    }

    public Object getPostObject() {
        return postObject;
    }

    public void setPostObject(Object postObject) {
        this.postObject = postObject;
    }
}
