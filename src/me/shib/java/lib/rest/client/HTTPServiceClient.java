package me.shib.java.lib.rest.client;

import me.shib.java.lib.common.utils.JsonLib;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Logger;

public final class HTTPServiceClient {

    private static final String userAgent = "Mozilla/5.0";
    private static final String charSet = "UTF-8";
    private static Logger logger = Logger.getLogger(HTTPServiceClient.class.getName());
    private JsonLib jsonLib;

    public HTTPServiceClient() {
        jsonLib = new JsonLib();
    }

    private static String readFully(InputStream in) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return new String(byteArrayOutputStream.toByteArray());
    }

    private String getRequestURLData(ArrayList<Parameter> params) throws UnsupportedEncodingException {
        StringBuilder requestDataBuilder = new StringBuilder();
        if ((null != params) && (params.size() > 0)) {
            for (int i = 0; i < params.size(); i++) {
                requestDataBuilder.append(params.get(i).getKey()).append("=").append(URLEncoder.encode(params.get(i).getValue(), charSet));
                if (i < (params.size() - 1)) {
                    requestDataBuilder.append("&");
                }
            }
        }
        return requestDataBuilder.toString();
    }

    private String getRequestURLData(Object object) throws UnsupportedEncodingException {
        return URLEncoder.encode(jsonLib.toJson(object), charSet);
    }

    protected ServiceResponse get(String requestURL, Object requestObject, ArrayList<Parameter> params)
            throws IOException {
        StringBuilder requestContentBuilder = new StringBuilder();
        if (null != requestObject) {
            String requestData = getRequestURLData(requestObject);
            if (!requestData.isEmpty()) {
                requestContentBuilder.append("/").append(requestData);
            }
        } else {
            String requestData = getRequestURLData(params);
            if (!requestData.isEmpty()) {
                requestContentBuilder.append("?").append(requestData);
            }
        }
        URL url = new URL(requestURL + requestContentBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setRequestProperty("Accept", "application/json");
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatusCode(conn.getResponseCode());
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output;
        StringBuilder outputBuilder = new StringBuilder();
        while ((output = br.readLine()) != null) {
            outputBuilder.append(output).append("\n");
        }
        br.close();
        conn.disconnect();
        serviceResponse.setResponse(outputBuilder.toString());
        return serviceResponse;
    }

    protected ServiceResponse post(String requestURL, Object requestObject, ArrayList<Parameter> params)
            throws IOException {
        if (null != requestObject) {
            ServiceResponse serviceResponse = new ServiceResponse();
            StringBuilder requestUrlBuilder = new StringBuilder();
            requestUrlBuilder.append(requestURL);
            if (params != null) {
                String urlParameters = getRequestURLData(params);
                if (!urlParameters.isEmpty()) {
                    requestUrlBuilder.append("?").append(urlParameters);
                }
            }
            URL url = new URL(requestUrlBuilder.toString());
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", userAgent);
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(jsonLib.toJson(requestObject));
            wr.flush();
            wr.close();
            serviceResponse.setStatusCode(conn.getResponseCode());
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            StringBuilder outputBuilder = new StringBuilder();
            while ((output = br.readLine()) != null) {
                outputBuilder.append(output).append("\n");
            }
            br.close();
            conn.disconnect();
            serviceResponse.setResponse(outputBuilder.toString());
            return serviceResponse;
        } else {
            return multipartPost(requestURL, params);
        }
    }

    private ServiceResponse multipartPost(String requestURL, ArrayList<Parameter> params) throws IOException {
        String boundary = "===" + System.currentTimeMillis() + "===";
        MultipartUtility multipart = new MultipartUtility(requestURL, charSet, boundary);
        if ((null != params) && (params.size() > 0)) {
            for (Parameter param : params) {
                if (null != param.getValue()) {
                    multipart.addFormField(param.getKey(), param.getValue());
                } else {
                    multipart.addFilePart(param.getKey(), param.getFile());
                }
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
                logger.throwing(this.getClass().getName(), "multipartPost", e);
                throw e;
            }
        }
        InputStream responseStream = code >= 400 ? connection.getErrorStream() : connection.getInputStream();
        ServiceResponse sr = new ServiceResponse();
        sr.setStatusCode(code);
        sr.setResponse(readFully(responseStream));
        connection.disconnect();
        return sr;
    }

}
