package me.shib.java.lib.restiny;

import me.shib.java.lib.restiny.requests.Request;
import me.shib.java.lib.restiny.util.JsonUtil;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * Create an instance for this to make a REST client
 */
public final class RESTinyClient {

    private static boolean trustingAllCertificates = false;

    private String endPoint;
    private JsonUtil jsonUtil;

    /**
     * Initializes a RESTinyClient with a given endpoint URI
     *
     * @param endPoint The base URI of the service where the API is hosted
     */
    public RESTinyClient(String endPoint) {
        this.endPoint = endPoint;
        this.jsonUtil = new JsonUtil();
    }

    public static synchronized void trustAllCerts() throws KeyManagementException, NoSuchAlgorithmException {
        if(!trustingAllCertificates) {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            trustingAllCertificates = true;
        }
    }

    /**
     * Makes a synchronous call to the service with the provided Request and its type
     *
     * @param request A request object. Currently there are two types supported - GET and POST
     * @return The Response object which gives necessary information on what happened
     * @throws IOException
     */
    public Response call(Request request) throws IOException {
        return new HTTPRequestThread(endPoint, request, null, jsonUtil).call();
    }

    /**
     * Makes an asynchronous call and invokes one of the callback methods based on the response
     *
     * @param request  A request object. Currently there are two types supported - GET and POST
     * @param callback The callback object that needs to be used after completing the asynchronous thread
     */
    public void asyncCall(Request request, Callback callback) {
        new HTTPRequestThread(endPoint, request, callback, jsonUtil).run();
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
