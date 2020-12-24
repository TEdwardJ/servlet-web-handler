package edu.ted.servlethandler.scanner;

import edu.ted.servlethandler.exception.InitializationException;
import edu.ted.servlethandler.interfaces.CanBeStarted;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Slf4j
public class WebAppWatchingScanner implements CanBeStarted {

    private WatchService watcher;
    private ExecutorService scheduledExecutor;
    private final File webappsDirectory;
    private final Consumer<File> listener;

    private WatchKey key;
    private Path observedDirectoryPath;

    public WebAppWatchingScanner(File webappsDirectory, Consumer<File> listener) {
        this.webappsDirectory = webappsDirectory;
        this.listener = listener;
    }

    public void init() {
        scheduledExecutor = Executors.newSingleThreadExecutor();
        observedDirectoryPath = Paths.get(webappsDirectory.toURI());
        try {
            watcher = FileSystems.getDefault().newWatchService();
            key = observedDirectoryPath.register(watcher,
                    ENTRY_CREATE);
        } catch (IOException x) {
            log.error("Error during WebAppScanner initialization", x);
            throw new InitializationException(x);
        }
    }

    public void start() {
        init();
        initialScan();
        scheduledExecutor.submit(this::scan);
    }

    private void initialScan() {
        for (File warFile : webappsDirectory.listFiles(f -> f.getName().endsWith(".war"))) {
            fileAdded(warFile);
        }
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

                    addedFile = observedDirectoryPath.resolve(addedFile);
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
        listener.accept(file);
    }

}
