package edu.ted.servlethandler;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import edu.ted.servlethandler.utils.FileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class WebServletHandlingITest {

    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private File dir;
    private DeploymentManager manager;
    private ServletHandler handlers;

    @BeforeEach
    public void init() {
        createWebAppDirectory();
        createHandler();
        createDeploymentManager();
        manager.init();
        manager.start();
    }

    private void createHandler() {
        handlers = new ServletHandler();
    }

    private void createDeploymentManager() {
        manager = new DeploymentManager(handlers, dir);
    }

    private void createWebAppDirectory() {
        dir = new File("webApps");
        dir.mkdir();
    }

    @Test
    public void test() throws InterruptedException, ServletException, IOException {
        SimpleHttpServletRequest httpRequest = new SimpleHttpServletRequest();

        OutputStream output = new ByteOutputStream();
        SimpleHttpServletResponse httpResponse = new SimpleHttpServletResponse(output);
        SimpleHttpServletResponse httpResponseSpy = Mockito.spy(httpResponse);
        httpRequest.getParameterMap().put("operand1", new String[]{"66"});
        httpRequest.getParameterMap().put("operand2", new String[]{"11"});
        httpRequest.getParameterMap().put("operation", new String[]{"subtract"});
        httpRequest.getParameterMap().put("more", new String[]{"0"});
        httpRequest.setRequestURI("/web-calculator-1.0-SNAPSHOT/calculator");
        httpRequest.setMethod("POST");

        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        when(httpResponseSpy.getWriter()).thenReturn(printWriter);

        executor.submit(() -> {
            try {
                putWarFileIntoWebApp();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        Thread.sleep(4000);
        handlers.handle(httpRequest, httpResponseSpy);
        assertEquals("55", writer.toString().trim());
        /*
        1. Wait for some time
        2. Put war file into scanned directory
        3. ensure handlers contain new app;
        4. Prepare HttpServletRequest as a mock, and HttpServletResponse too
        5. handle prepared request by handler
        6. Try to get answer from servlet*/
    }

    private void putWarFileIntoWebApp() throws URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource("web-calculator-1.0-SNAPSHOT.war");
        File warFile = new File(resource.toURI());
        FileManager.copy(warFile.getPath(), dir.getPath());
    }

    @AfterEach
    public void destroy() {
        manager.stop();
        removeWebAppDirectory();
    }

    private void removeWebAppDirectory() {
        FileManager.remove(dir.getPath());
    }
}