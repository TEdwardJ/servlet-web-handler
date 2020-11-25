package edu.ted.servlethandler;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class WarUnwrapper {

    public static File unwrap(File warFile, String destDir) {
        File tempDestDir = new File(destDir);
        return unwrap(warFile, tempDestDir);
    }

    private static File unwrap(File warFile, File destDir) {
        byte[] buffer = new byte[8192];
        if (!destDir.exists() && !destDir.mkdir()) {
            return null;
        }
        try (FileInputStream in = new FileInputStream(warFile);
             ZipInputStream warInputStream = new ZipInputStream(in)) {
            ZipEntry zipEntry;
            while ((zipEntry = warInputStream.getNextEntry()) != null) {
                log.debug("zipEntry {}", zipEntry);
                if (zipEntry.isDirectory()) {
                    File dir = new File(destDir, zipEntry.getName());
                    if (!Files.exists(dir.toPath())) {
                        Files.createDirectories(dir.toPath());
                    }
                } else {
                    File newFile = newFile(destDir, zipEntry);
                    try (FileOutputStream destinationStream = new FileOutputStream(newFile)) {
                        int length;
                        while ((length = warInputStream.read(buffer)) > 0) {
                            destinationStream.write(buffer, 0, length);
                        }
                    }
                }
            }
            warInputStream.closeEntry();
            return destDir;
        } catch (FileNotFoundException e) {
            log.error("File {} was not found", warFile, e);
        } catch (IOException e) {
            log.error("Some unexpected exception during unwrapping of {}", warFile, e);
        }
        return null;
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        //log.debug("destinationDir {}", destinationDir);
        checkPath(destFile);

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private static void checkPath(File destinationDir) {
        String path = destinationDir.getPath();
        File dir = new File(path.substring(0, path.lastIndexOf("\\")));
        if (!dir.exists()) {
            checkPath(dir);
            dir.mkdir();
        }
    }
}
