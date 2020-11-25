package edu.ted.servlethandler.utils;



import java.io.*;

import static edu.ted.servlethandler.utils.FileSystemItemType.DIRECTORY;
import static edu.ted.servlethandler.utils.FileSystemItemType.FILE;


public class FileManager {
    private static final File[] EMPTY_FILE_ARRAY = new File[]{};
    /**
     * рекурсивно подсчитывает количество директорий по указанному пути
     *
     * @param path путь к папке
     * @return количество папок в папке и всех подпапках по пути
     */
    public static int countDirs(String path) {
        return countChildFileSystemItems(new File(path), DIRECTORY);
    }

    /**
     * перемещает файлы и папки
     *
     * @param from путь к файлу или папке
     * @param to   путь к папке куда будет производиться копирование
     */
    public static void move(String from, String to) {
        new File(from).renameTo(new File(to));
    }

    /**
     * подсчитывает рекурсивно файлы по указанному пути
     *
     * @param path путь к папке
     * @return количество файлов в папке и всех подпапках по пути
     */
    public static int countFiles(String path) {
        return countChildFileSystemItems(new File(path), FILE);
    }

    /**
     * метод по удалению папок и файлов.
     * Параметр from - путь к файлу или папке
     */
    public static void remove(String from) {
        File startFileSystemItem = new File(from);
        removeFileSystemItemsRecursively(startFileSystemItem);
    }

    /**
     * метод по копированию папок и файлов.
     * Параметр from - путь к файлу или папке, параметр to - путь к папке куда будет производиться копирование
     */
    public static void copy(String from, String to) {
        copyFileSystemItems(new File(from), to);
    }

    private static File[] getChildFileSystemItems(File fileSystemItem) {
        File[] childFileSystemItemsArray = fileSystemItem.listFiles();
        if (childFileSystemItemsArray == null) {
            return EMPTY_FILE_ARRAY;
        }
        return childFileSystemItemsArray;
    }

    private static int countChildFileSystemItems(File fileSystemItem, FileSystemItemType itemTypeToBeCounted) {
        if (fileSystemItem.isDirectory()) {
            int counter = 0;
            int takeDirectoriesIntoCount = itemTypeToBeCounted == FILE ? 0 : 1;
            //Loop to count recursively in hierarchy
            for (File item : getChildFileSystemItems(fileSystemItem)) {
                if (item.isDirectory()) {
                    counter += takeDirectoriesIntoCount + countChildFileSystemItems(item, itemTypeToBeCounted);
                } else if (itemTypeToBeCounted != DIRECTORY) {
                    counter ++;
                }
            }
            return counter;
        }
        return 0;
    }

    private static void copyFileSystemItems(File fileSystemItem, String to) {
        if (fileSystemItem.isDirectory()) {
            File destinationDir = copyDir(fileSystemItem, to);

            for (File item : getChildFileSystemItems(fileSystemItem)) {
                copyFileSystemItems(item, destinationDir.getPath());
            }
            fileSystemItem.delete();
        } else if (fileSystemItem.isFile()) {
            copyFile(fileSystemItem, to);
        }
    }

    private static void removeFileSystemItemsRecursively(File fileSystemItem) {
        for (File item : getChildFileSystemItems(fileSystemItem)) {
            removeFileSystemItemsRecursively(item);
            item.delete();
        }
        fileSystemItem.delete();
    }

    private static void copyFile(File source, String to) {
        byte[] buffer = new byte[1024];
        File destination = new File(to);
        if (destination.isDirectory()) {
            destination = new File(to + File.separator + source.getName());
        }
        try (InputStream sourceFile = new BufferedInputStream(new FileInputStream(source));
             OutputStream destinationStream = new BufferedOutputStream(new FileOutputStream(destination))) {
            int count;
            while ((count = sourceFile.read(buffer)) != -1) {
                destinationStream.write(buffer, 0, count);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File copyDir(File sourceDir, String to) {
        File destinationDir = new File(to + File.separator + sourceDir.getName());
        if (!destinationDir.exists()) {
            if (destinationDir.mkdir()) {
                return destinationDir;
            }
        }
        return destinationDir;
    }
}

