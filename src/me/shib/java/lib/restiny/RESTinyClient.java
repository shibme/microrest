package me.shib.java.lib.restiny;

import me.shib.java.lib.common.utils.JsonLib;
import me.shib.java.lib.restiny.requests.Request;

import java.io.IOException;

/**
 * Create an instance for this to make a REST client
 */
public final class RESTinyClient {

    private String endPoint;
    private JsonLib jsonLib;

    /**
     * Initializes a RESTinyClient with a given endpoint URI
     *
     * @param endPoint The base URI of the service where the API is hosted
     */
    public RESTinyClient(String endPoint) {
        this.endPoint = endPoint;
        this.jsonLib = new JsonLib();
    }

    /**
     * Makes a synchronous call to the service with the provided Request and its type
     *
     * @param request A request object. Currently there are two types supported - GET and POST
     * @return The Response object which gives necessary information on what happened
     * @throws IOException
     */
    public Response call(Request request) throws IOException {
        return new HTTPRequestThread(endPoint, request, null, jsonLib).call();
    }

    /**
     * Makes an asynchronous call and invokes one of the callback methods based on the response
     *
     * @param request  A request object. Currently there are two types supported - GET and POST
     * @param callback The callback object that needs to be used after completing the asynchronous thread
     */
    public void asyncCall(Request request, Callback callback) {
        new HTTPRequestThread(endPoint, request, callback, jsonLib).run();
    }

    /**
     * Makes an asynchronous call without any callback
     *
     * @param request A request object. Currently there are two types supported - GET and POST
     */
    public void asyncCall(Request request) {
        asyncCall(request, null);
    }

    /**
     * A simple callback interface with methods that invoke on Response and Exception
     */
    public interface Callback {
        public void onResponse(Response response);

        public void onException(IOException e);
    }


}
