package edu.ted.servlethandler.entity;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Objects;

public class SimpleHttpServletResponse extends SimpleHttpServletResponseAdapter {

    private OutputStream output;
    private PrintWriter writer;

    public SimpleHttpServletResponse(OutputStream output) {
        this.output = output;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if(Objects.isNull(writer)){
            writer = new PrintWriter(output);
        }
        return writer;
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

}
