package com.bdd.utils;

import com.bdd.config.ConfigReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Enterprise File Download Manager.
 * Validates, polls, and manages file downloads (PDF, CSV, Excel, Images)
 * in headless, local, and remote Grid executions without native OS dialogs.
 */
public class FileManager {

    private static final String DEFAULT_DOWNLOAD_DIR = ConfigReader.getDownloadDirectory();

    public static File getDownloadDir() {
        File dir = new File(DEFAULT_DOWNLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * Polls the download directory until the specified file is completely downloaded (and not .crdownload or .part).
     *
     * @param fileName       Exact file name or partial match to look for
     * @param timeoutSeconds Maximum duration to wait in seconds
     * @return The downloaded File object
     */
    public static File waitForFileDownloaded(String fileName, int timeoutSeconds) {
        File downloadDir = getDownloadDir();
        Instant deadline = Instant.now().plus(Duration.ofSeconds(timeoutSeconds));

        while (Instant.now().isBefore(deadline)) {
            File[] files = downloadDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    // Ignore in-progress Chrome or Firefox partial downloads
                    if (name.endsWith(".crdownload") || name.endsWith(".part") || name.endsWith(".tmp")) {
                        continue;
                    }
                    if (name.contains(fileName) && file.length() > 0) {
                        return file;
                    }
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        throw new RuntimeException("File '" + fileName + "' was not found in '" + downloadDir.getAbsolutePath() + "' after " + timeoutSeconds + "s.");
    }

    /**
     * Polls the download directory for any file with the specified extension.
     */
    public static File waitForFileWithExtension(String extension, int timeoutSeconds) {
        String cleanExt = extension.startsWith(".") ? extension : "." + extension;
        File downloadDir = getDownloadDir();
        Instant deadline = Instant.now().plus(Duration.ofSeconds(timeoutSeconds));

        while (Instant.now().isBefore(deadline)) {
            File[] files = downloadDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    if (name.endsWith(".crdownload") || name.endsWith(".part") || name.endsWith(".tmp")) {
                        continue;
                    }
                    if (name.toLowerCase().endsWith(cleanExt.toLowerCase()) && file.length() > 0) {
                        return file;
                    }
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        throw new RuntimeException("File with extension '" + extension + "' was not found in '" + downloadDir.getAbsolutePath() + "' after " + timeoutSeconds + "s.");
    }

    public static boolean isFileDownloaded(String fileName) {
        File downloadDir = getDownloadDir();
        File[] files = downloadDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().contains(fileName) && file.length() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Cleans up all files from the download directory before or after test execution.
     */
    public static void cleanDownloadFolder() {
        File downloadDir = getDownloadDir();
        File[] files = downloadDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    /**
     * Reads all text content from a downloaded file (e.g. CSV or text log).
     */
    public static String readFileContent(File file) throws IOException {
        return Files.readString(file.toPath());
    }
}
