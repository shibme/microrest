package me.shib.java.lib.rest.client;

import java.io.File;

public final class Parameter {

    private String key;
    private String value;
    private File file;

    public Parameter(String key, String value) {
        this.key = key;
        this.value = value;
        this.file = null;
    }

    public Parameter(String key, File file) {
        this.key = key;
        this.value = null;
        this.file = file;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public File getFile() {
        return file;
    }

}
