package edu.ted.servlethandler.io;

import edu.ted.servlethandler.entity.SimpleHttpServletResponse;
import edu.ted.servlethandler.utils.Constants;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import static edu.ted.servlethandler.utils.Constants.EOL;

public class ResponseWriter extends PrintWriter {

    public ResponseWriter(Writer out) {
        super(out);
    }

    public static void writeHeader(SimpleHttpServletResponse response) throws IOException {
        StringBuilder headerText = new StringBuilder();
        ServletOutputStream out = response.getOutputStream();
        headerText
                .append(Constants.HTTP_VERSION)
                .append(" ")
                .append(response.getStatus())
                .append(EOL);
        for (String headerName : response.getHeaderNames()) {
            headerText
                    .append(headerName)
                    .append(": ")
                    .append(response.getHeader(headerName));
            headerText.append(EOL);
        }
        headerText.append(EOL);
        out.write(headerText.toString().getBytes());
/*        if (response.getHeader("Content-Length") != null) {
            out.write(response.getBinaryBody());
        };*/
    }

    public static void write(SimpleHttpServletResponse response) throws IOException {
        SimpleServletOutputStream outputStream = (SimpleServletOutputStream) response.getOutputStream();
        outputStream.setCompleted(true);
        ///outputStream.setHeadersAreWritten(false);
        writeHeader(response);
        outputStream.setHeadersAreWritten(true);
        //outputStream.flush();
    }
}
