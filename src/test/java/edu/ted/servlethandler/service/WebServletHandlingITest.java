package edu.ted.servlethandler.service;

import edu.ted.servlethandler.WebServer;
import edu.ted.servlethandler.utils.FileManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.maven.shared.invoker.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class WebServletHandlingITest {

    private static final WebServer server = new WebServer(3000);
    private static File dir;

    @BeforeAll
    public static void init() throws MavenInvocationException, URISyntaxException {
        buildTestWar();
        createWebAppDirectory();
        putWarFileIntoWebApp();
        new Thread(server::start).start();
    }

    @AfterAll
    public static void destroy() {
        server.stop();
        removeWebAppDirectory();
    }

    private static void buildTestWar() throws MavenInvocationException {
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

    private static void createWebAppDirectory() {
        dir = new File("webapps");
        dir.mkdir();
    }

    private static void putWarFileIntoWebApp() throws URISyntaxException {
        final URL resource = WebServletHandlingITest.class.getClassLoader().getResource("WebCalculator/target/web-calculator-1.0-SNAPSHOT.war");
        File warFile = new File(resource.toURI());
        FileManager.copy(warFile.getPath(), dir.getPath());
    }

    private static void removeWebAppDirectory() {
        FileManager.remove(dir.getPath());
    }

    @Test
    public void givenServerSendPostRequest_whenRequestedResult_thenCorrect() throws InterruptedException, IOException {

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("operand1","66")
                .add("operand2","11")
                .add("operation","subtract")
                .add("more","0")
                .build();
        Request request = new Request
                .Builder()
                .url("http://127.0.0.1:3000/web-calculator-1.0-SNAPSHOT/calculator")
                .method("POST", requestBody)
                .build();

        Response response = client.newCall(request).execute();
        String body = response.body().string();
        assertEquals("55", body.trim());
    }

    @Test
    public void givenServerSendPostRequest_whenExceptionInServletAndShownInResponse_thenCorrect() throws InterruptedException, IOException {

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("operand1","66")
                .add("operand2","0")
                .add("operation","divide")
                .add("more","0")
                .build();
        Request request = new Request
                .Builder()
                .url("http://127.0.0.1:3000/web-calculator-1.0-SNAPSHOT/calculator")
                .method("POST", requestBody)
                .build();

        Response response = client.newCall(request).execute();
        String body = response.body().string();
        assertEquals(500, response.code());
        assertTrue( body.contains("java.lang.ArithmeticException: / by zero"));
    }

    @Test
    public void givenServerSendGetRequest_whenRequestedResult_thenCorrect() throws IOException, InterruptedException {
        OkHttpClient client = new OkHttpClient();
        Thread.sleep(1000);
        Request request = new Request.Builder().url("http://127.0.0.1:3000/web-calculator-1.0-SNAPSHOT/").build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("text/html; charset=utf-8", response.header("Content-Type"));
        String body = response.body().string();

        System.out.println(body);
    }
}
