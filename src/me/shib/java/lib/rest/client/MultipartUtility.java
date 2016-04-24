package me.shib.java.lib.rest.client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public final class MultipartUtility {

    private static final String LINE_FEED = "\r\n";
    private static final String userAgent = "Mozilla/5.0";

    private final String boundary;
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    protected MultipartUtility(String requestURL, String charset, String boundary) throws IOException {
        this.charset = charset;
        this.boundary = boundary;

        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setChunkedStreamingMode(0); // Chunked transfer mode to prevent
        // local buffering
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent", userAgent);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }

    public void addFormField(String name, String value) {
        writer.append("--").append(boundary).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"")
                .append(name).append("\"").append(LINE_FEED)
                .append("Content-Type: text/plain; charset=")
                .append(charset).append(LINE_FEED)
                .append(LINE_FEED).append(value).append(LINE_FEED);
        writer.flush();
    }

    public void addFilePart(String fieldName, File uploadFile) throws IOException {
        String fileName = uploadFile.getName();
        FileInputStream inputStream = new FileInputStream(uploadFile);
        addFilePart(fieldName, inputStream, fileName);
    }

    public void addFilePart(String fieldName, InputStream inputStream, String fileName) throws IOException {
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
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

    public HttpURLConnection execute() throws IOException {
        writer.append("--").append(boundary).append("--").append(LINE_FEED);
        writer.close();
        return httpConn;
    }
}