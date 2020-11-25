package edu.ted.servlethandler.scanner;

import edu.ted.servlethandler.WebApplicationProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class Scanner {

    @Setter
    @Getter
    private long interval;
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    private String observedDirectory;
    private WebApplicationProvider.ScannerListener listener;

    private volatile Set<File> filesSet = new HashSet<>();

    public Scanner(String observedDirectory, WebApplicationProvider.ScannerListener listener) {
        this.observedDirectory = observedDirectory;
        this.listener = listener;
    }

    public void start() {
        scheduledExecutor.scheduleAtFixedRate(() -> scan(), 1, getInterval(), TimeUnit.SECONDS);
    }

    public void scan() {
        File directory = new File(observedDirectory);

        Set<File> currentFilesSet = new HashSet<>();
        getFilesSet(directory, currentFilesSet);
        log.debug("Old File Collection: {}", filesSet);
        currentFilesSet.removeAll(filesSet);
        currentFilesSet.forEach(f -> fileAdded(f));
        if (currentFilesSet.size() > 0) {
            filesSet = currentFilesSet;
        }
    }

    public void stop() {
        scheduledExecutor.shutdown();
    }

    private void getFilesSet(File directory, Set<File> filesSet) {
        filesSet.addAll(Arrays.stream(directory.listFiles()).collect(Collectors.toSet()));
    }

    void fileAdded(File file) {
        log.debug("File added: {}, {}", file, file.hashCode());
        listener.fileAdded(file);
    }


}
