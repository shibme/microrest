package me.shib.java.lib.restiny;

import me.shib.java.lib.restiny.requests.GET;
import me.shib.java.lib.restiny.requests.POST;
import me.shib.java.lib.restiny.requests.Request;
import me.shib.java.lib.restiny.util.JsonUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

class HTTPRequestThread extends Thread {

    private static final String LINE_FEED = "\r\n";
    private static final String userAgent = "Mozilla/5.0";
    private static final String charSet = "UTF-8";
    private static final Logger logger = Logger.getLogger(HTTPRequestThread.class.getName());
    private JsonUtil jsonUtil;
    private String endPoint;
    private Request request;
    private RESTinyClient.Callback callback;

    HTTPRequestThread(String endPoint, Request request, RESTinyClient.Callback callback, JsonUtil jsonUtil) {
        this.endPoint = endPoint;
        this.request = request;
        this.callback = callback;
        this.jsonUtil = jsonUtil;
    }

    Response call() throws IOException {
        switch (request.getRequestType()) {
            case GET:
                return getRequest();
            case POST:
                POST postRequest = (POST) request;
                if (null != postRequest.getPostObject()) {
                    return postStandardRequest(postRequest);
                } else {
                    return postMultipartRequest(postRequest);
                }
            default:
                throw new IOException("Invalid/Null Request Type");
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            Response response = call();
            if (callback != null) {
                callback.onResponse(response);
            }
        } catch (IOException e) {
            if (callback != null) {
                callback.onException(e);
            }
        }
    }

    private String getRequestURLWithParameters(Map<String, String> parameters) throws UnsupportedEncodingException {
        StringBuilder requestDataBuilder = new StringBuilder();
        if (null != parameters) {
            Set<String> keys = parameters.keySet();
            boolean first = true;
            for (String key : keys) {
                String value = parameters.get(key);
                if (value != null) {
                    if (first) {
                        first = false;
                    } else {
                        requestDataBuilder.append("&");
                    }
                    requestDataBuilder.append(key).append("=").append(URLEncoder.encode(parameters.get(key), charSet));
                }
            }
        }
        return requestDataBuilder.toString();
    }

    private Response getRequest() throws IOException {
        GET getRequest = (GET) request;
        StringBuilder requestUrlBuilder = new StringBuilder();
        String requestData = getRequestURLWithParameters(getRequest.getParameters());
        if (!requestData.isEmpty()) {
            requestUrlBuilder.append("?").append(requestData);
        }
        URL url = new URL(getRequest.getUrl(endPoint) + requestUrlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setRequestProperty("Accept", "application/json");
        Map<String, String> requestProperties = getRequest.getRequestProperties();
        Set<String> propertyKeys = requestProperties.keySet();
        for (String key : propertyKeys) {
            String value = requestProperties.get(key);
            if (value != null) {
                conn.setRequestProperty(key, value);
            }
        }
        Response response = new Response(jsonUtil);
        response.setStatusCode(conn.getResponseCode());
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output;
        StringBuilder outputBuilder = new StringBuilder();
        while ((output = br.readLine()) != null) {
            outputBuilder.append(output).append(LINE_FEED);
        }
        br.close();
        conn.disconnect();
        response.setResponse(outputBuilder.toString());
        return response;
    }

    private Response postStandardRequest(POST postRequest) throws IOException {
        Response response = new Response(jsonUtil);
        StringBuilder requestUrlBuilder = new StringBuilder();
        requestUrlBuilder.append(postRequest.getUrl(endPoint));
        String requestData = getRequestURLWithParameters(postRequest.getStringParameters());
        if (!requestData.isEmpty()) {
            requestUrlBuilder.append("?").append(requestData);
        }
        URL url = new URL(requestUrlBuilder.toString());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(jsonUtil.toJson(postRequest.getPostObject()));
        wr.flush();
        wr.close();
        response.setStatusCode(conn.getResponseCode());
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output;
        StringBuilder outputBuilder = new StringBuilder();
        while ((output = br.readLine()) != null) {
            outputBuilder.append(output).append(LINE_FEED);
        }
        br.close();
        conn.disconnect();
        response.setResponse(outputBuilder.toString());
        return response;
    }

    private String readFully(InputStream in) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return new String(byteArrayOutputStream.toByteArray());
    }

    private Response postMultipartRequest(POST postRequest) throws IOException {
        String boundary = "===" + System.currentTimeMillis() + "===";
        MultipartUtility multipart = new MultipartUtility(postRequest.getUrl(endPoint), charSet, boundary);
        Map<String, String> stringParameters = postRequest.getStringParameters();
        Set<String> stringParameterKeys = stringParameters.keySet();
        Map<String, File> fileParameters = postRequest.getFileParameters();
        Set<String> fileParameterKeys = fileParameters.keySet();
        for (String key : stringParameterKeys) {
            String value = stringParameters.get(key);
            if (value != null) {
                multipart.addFormField(key, value);
            }
        }
        for (String key : fileParameterKeys) {
            File value = fileParameters.get(key);
            if (value != null) {
                multipart.addFilePart(key, value);
            }
        }
        Map<String, String> requestProperties = postRequest.getRequestProperties();
        Set<String> propertyKeys = requestProperties.keySet();
        for (String key : propertyKeys) {
            String value = requestProperties.get(key);
            if (value != null) {
                multipart.setRequestProperty(key, value);
            }
        }
        HttpURLConnection connection = multipart.execute();
        int code;
        try {
            code = connection.getResponseCode();
        } catch (IOException e) {
            if (e.getMessage().equals("No authentication challenges found")) {
                code = 401;
            } else {
                logger.throwing(this.getClass().getName(), "postMultipartRequest", e);
                throw e;
            }
        }
        InputStream responseStream = code >= 400 ? connection.getErrorStream() : connection.getInputStream();
        Response response = new Response(jsonUtil);
        response.setStatusCode(code);
        response.setResponse(readFully(responseStream));
        connection.disconnect();
        return response;
    }

}
