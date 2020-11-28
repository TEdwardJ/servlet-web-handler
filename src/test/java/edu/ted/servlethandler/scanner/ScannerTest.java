package edu.ted.servlethandler.scanner;

import edu.ted.servlethandler.WebApplicationProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class ScannerTest {

    private File dir;
    private volatile File fileAdded;

    @BeforeEach
    public void before() {
        dir = new File("forScan");
        dir.mkdir();
    }

    @AfterEach
    public void after() {
        fileAdded.delete();
        dir.delete();
    }

    @Test
    public void testFileAdditionUnderSpecifiedDirectory() throws InterruptedException, IOException {
        Consumer<File> listener = (File file) -> {
                log.debug("Added File {}", file);
                fileAdded = file; };
        WebAppWatchingScanner scanner = new WebAppWatchingScanner("forScan", listener);
        scanner.init();
        scanner.start();
        Thread.sleep(2000);
        File addedFile = createFile("forScan/file.war", "testFile");
        Thread.sleep(5000);
        assertEquals("file.war", fileAdded.getName());
    }


    private File createFile(String path, String contentText) throws IOException {
        File fileToBeCreated = new File(path);
        if (contentText == null || contentText.length() == 0) {
            fileToBeCreated.createNewFile();
            return fileToBeCreated;
        }
        try (FileWriter writer = new FileWriter(fileToBeCreated)) {
            writer.write(contentText);
        }
        return fileToBeCreated;
    }

}