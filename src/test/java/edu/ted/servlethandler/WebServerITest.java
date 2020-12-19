package edu.ted.servlethandler;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebServerITest {

    private final OkHttpClient client = new OkHttpClient();
    private final String rootDirectory = System.getProperty("user.dir") + "/src/main/resources/webapp";
    private final WebServer server = new WebServer(3000);

    @Test
    public void givenServerSendRequest_whenRequestedResult_thenCorrect() throws IOException, InterruptedException {

        new Thread(server::start).start();
        Thread.sleep(8500);
        Request request = new Request.Builder().url("http://127.0.0.1:3000/web-calculator-1.0-SNAPSHOT/").build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("text/html; charset=utf-8", response.header("Content-Type"));
        final String body = response.body().string();

        System.out.println(body);
    }

}