package me.shib.java.lib.restiny;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

final class MultipartUtility {

    private static final String LINE_FEED = "\r\n";
    private static final String userAgent = "Mozilla/5.0";

    private final String boundary;
    private HttpURLConnection connection;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    protected MultipartUtility(String requestURL, Map<String, String> requestHeaders, String charset) throws IOException {
        this.charset = charset;
        this.boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        URL url = new URL(requestURL);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("User-Agent", userAgent);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        setRequestProperties(requestHeaders);
        outputStream = connection.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }


    private void setRequestProperties(Map<String, String> requestProperties) {
        Set<String> propertyKeys = requestProperties.keySet();
        for (String key : propertyKeys) {
            String value = requestProperties.get(key);
            if (value != null) {
                connection.setRequestProperty(key, value);
            }
        }
    }

    protected void setParameters(Map<String, String> parameters) {
        Set<String> parameterKeys = parameters.keySet();
        for (String name : parameterKeys) {
            String value = parameters.get(name);
            if (value != null) {
                writer.append("--").append(boundary).append(LINE_FEED)
                        .append("Content-Disposition: form-data; name=\"")
                        .append(name).append("\"").append(LINE_FEED)
                        .append("Content-Type: text/plain; charset=")
                        .append(charset).append(LINE_FEED)
                        .append(LINE_FEED).append(value).append(LINE_FEED);
                writer.flush();
            }
        }
    }

    protected void setFiles(Map<String, File> fileParameters) throws IOException {
        Set<String> fileParameterKeys = fileParameters.keySet();
        for (String fieldName : fileParameterKeys) {
            File file = fileParameters.get(fieldName);
            if ((file != null) && file.exists()) {
                String fileName = file.getName();
                FileInputStream fileInputStream = new FileInputStream(file);
                addFilePart(fieldName, fileInputStream, fileName);
            }
        }
    }

    private void addFilePart(String fieldName, FileInputStream fileInputStream, String fileName) throws IOException {
        writer.append("--").append(boundary).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"")
                .append(fieldName).append("\"; filename=\"")
                .append(fileName).append("\"").append(LINE_FEED).append("Content-Type: ")
                .append(URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED).append("Content-Transfer-Encoding: binary")
                .append(LINE_FEED).append(LINE_FEED);
        writer.flush();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        fileInputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

    protected HttpURLConnection close() {
        writer.append("--").append(boundary).append("--").append(LINE_FEED);
        writer.close();
        return connection;
    }
}