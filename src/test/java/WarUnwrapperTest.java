import edu.ted.servlethandler.WarUnwrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


class WarUnwrapperTest {

    private File destDir;

    @AfterEach
    public void after(){
        destDir.delete();
    }

    @Test
    void unwrap() throws IOException, URISyntaxException {
        final String sourceWarPath = "web-calculator-1.0-SNAPSHOT.war";
        URL sourceWarResource = getClass().getClassLoader().getResource(sourceWarPath);
        final String destPath = "C:\\Users\\Ted\\AppData\\Local\\Temp\\testWebApp";
        destDir = new File(destPath);
        destDir.mkdir();
        WarUnwrapper.unwrap(new File(sourceWarResource.toURI()), destPath);
        //TODO: look at lab about File System operations in order to simplify this test as much as possible
    }
}