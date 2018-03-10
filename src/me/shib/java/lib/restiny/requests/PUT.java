package me.shib.java.lib.restiny.requests;

public final class PUT extends POST {

    public PUT(String methodName) {
        super(methodName);
        this.setRequestType(RequestType.PUT);
    }
}
