package edu.ted.servlethandler.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class WarUnwrapperTest {

    private String destinationPath;

    @AfterEach
    public void after() {
        FileManager.remove(destinationPath);
    }

    @BeforeEach
    private void init() throws MavenInvocationException {
        prepareDestinationDirectory();
        buildTestWar();
    }

    private void buildTestWar() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("src/test/resources/WebCalculator/pom.xml"));
        request.setBaseDirectory(new File("src/test/resources/WebCalculator"));
        request.setGoals(Collections.unmodifiableList(Arrays.asList("clean", "install")));
        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0) {
            log.debug("Some error while building test servlet project: ", result.getExecutionException());
            throw new IllegalStateException("Build failed");
        }
    }

    private void prepareDestinationDirectory() {
        String osTempDirectoryPath = System.getProperty("java.io.tmpdir");
        destinationPath = osTempDirectoryPath + "\\testWebApp";

        File destinationDir = new File(destinationPath);
        if (destinationDir.exists()) {
            FileManager.remove(destinationPath);
        }
        destinationDir.mkdir();
    }

    @Test
    void unwrap() throws URISyntaxException {
        final String sourceWarPath = "WebCalculator/target/web-calculator-1.0-SNAPSHOT.war";
        URL sourceWarResource = getClass().getClassLoader().getResource(sourceWarPath);

        WarUnwrapper.unwrap(new File(sourceWarResource.toURI()), destinationPath);
        assertTrue(FileManager.countDirs(destinationPath) > 0);
        assertTrue(FileManager.countFiles(destinationPath) > 0);
    }
}