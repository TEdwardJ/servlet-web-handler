package edu.ted.servlethandler.scanner;

import edu.ted.servlethandler.WebApplicationProvider;
import edu.ted.servlethandler.exception.InitializationException;
import edu.ted.servlethandler.interfaces.CanBeStarted;
import edu.ted.servlethandler.interfaces.ShouldBeInitialized;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Slf4j
public class WebAppWatchingScanner implements CanBeStarted, ShouldBeInitialized {

    private WatchService watcher;


    private ExecutorService scheduledExecutor;
    private final String observedDirectory;
    private final WebApplicationProvider.ScannerListener listener;

    private WatchKey key;
    private Path observedDirPath;

    public WebAppWatchingScanner(String observedDirectory, WebApplicationProvider.ScannerListener listener) {
        this.observedDirectory = observedDirectory;
        this.listener = listener;
    }

    public void init() {
        scheduledExecutor = Executors.newSingleThreadExecutor();
        observedDirPath = Paths.get(observedDirectory);
        try {
            watcher = FileSystems.getDefault().newWatchService();
            key = observedDirPath.register(watcher,
                    ENTRY_CREATE);
        } catch (IOException x) {
            log.error("Error during WebAppScanner initialization", x);
            throw new InitializationException(x);
        }
    }

    public void start() {
        scheduledExecutor.submit(this::scan);
    }

    public void scan() {
        for (; ; ) {
            try {
                key = watcher.take();
            } catch (InterruptedException e) {
                return;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == OVERFLOW) {
                    continue;
                }
                if (kind == ENTRY_CREATE) {
                    WatchEvent<Path> currentEvent = (WatchEvent<Path>) event;
                    Path addedFile = currentEvent.context();

                    addedFile = observedDirPath.resolve(addedFile);
                    fileAdded(addedFile.toFile());
                }
            }
        }
    }

    public void stop() {
        scheduledExecutor.shutdown();
    }

    void fileAdded(File file) {
        log.debug("File added: {}, {}", file, file.hashCode());
        listener.fileAdded(file);
    }


}
