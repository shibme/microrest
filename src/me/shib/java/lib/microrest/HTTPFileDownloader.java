package me.shib.java.lib.microrest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HTTPFileDownloader extends Thread {

    private static Logger logger = Logger.getLogger(HTTPFileDownloader.class.getName());

    private String downloadURL;
    private String downloadDirectoryPath;
    private File file;
    private DownloadProgress downloadProgress;

    public HTTPFileDownloader(String downloadURL) {
        downloadInitializer(downloadURL, null, null);
    }

    public HTTPFileDownloader(String downloadURL, String downloadDirectoryPath) {
        downloadInitializer(downloadURL, downloadDirectoryPath, null);
    }

    public HTTPFileDownloader(String downloadURL, File file) {
        downloadInitializer(downloadURL, null, file);
    }

    public DownloadProgress getDownloadProgress() {
        return downloadProgress;
    }

    private void downloadInitializer(String downloadURL, String downloadDirectoryPath, File file) {
        this.downloadProgress = new DownloadProgress();
        this.downloadURL = downloadURL;
        this.downloadDirectoryPath = downloadDirectoryPath;
        this.file = file;
    }

    private boolean prepareDownloadPath(File downloadFileDir) {
        if (!(downloadFileDir.exists() && downloadFileDir.isDirectory())) {
            if (!downloadFileDir.mkdirs()) {
                logger.log(Level.WARNING, "Failed to create directory tree for: " + downloadFileDir.getAbsolutePath());
            }
        }
        return true;
    }

    private File downloadFile() throws IOException {
        downloadProgress.status = DownloadStatus.DOWNLOADING;
        URL url = new URL(downloadURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String actualFileName = new File(url.getFile()).getName();
        actualFileName = actualFileName.replaceAll("[^[^/]*]+/", "");
        if (file == null) {
            if (downloadDirectoryPath != null) {
                if (!prepareDownloadPath(new File(downloadDirectoryPath))) {
                    downloadDirectoryPath = "";
                }
            }
            file = new File(downloadDirectoryPath + File.separator + actualFileName);
        } else {
            if (file.getParent() != null) {
                prepareDownloadPath(new File(file.getParent()));
            }
        }
        downloadProgress.fileSize = connection.getContentLength();
        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
        byte[] data = new byte[1024];
        int i;
        while (((i = in.read(data, 0, 1024)) >= 0) && (downloadProgress.status == DownloadStatus.DOWNLOADING)) {
            downloadProgress.downloadedSize += i;
            bout.write(data, 0, i);
            downloadProgress.completedPercentage = (downloadProgress.downloadedSize * 100) / downloadProgress.fileSize;
        }
        bout.close();
        in.close();
        if ((file.exists()) && (downloadProgress.status == DownloadStatus.DOWNLOADING)) {
            downloadProgress.status = DownloadStatus.COMPLETED;
            return file;
        }
        if (file.exists()) {
            if (file.delete()) {
                logger.log(Level.INFO, "Deleted the incomplete file " + file.getAbsolutePath());
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            downloadProgress.downloadedFile = downloadFile();
        } catch (IOException e) {
            logger.throwing(this.getClass().getName(), "run", e);
            downloadProgress.downloadedFile = null;
        }
    }

    public enum DownloadStatus {
        NOT_STARTED, DOWNLOADING, COMPLETED, CANCELLED;
    }

    public class DownloadProgress {
        private int fileSize;
        private int downloadedSize;
        private int completedPercentage;
        private File downloadedFile;
        private DownloadStatus status;

        private DownloadProgress() {
            this.fileSize = 0;
            this.downloadedSize = 0;
            this.completedPercentage = 0;
            downloadedFile = null;
            status = DownloadStatus.NOT_STARTED;
        }

        public int getFileSize() {
            return fileSize;
        }

        public int getDownloadedSize() {
            return downloadedSize;
        }

        public int getCompletedPercentage() {
            return completedPercentage;
        }

        public File getDownloadedFile() {
            return downloadedFile;
        }

        public DownloadStatus getStatus() {
            return status;
        }
    }

}
