package edu.ted.servlethandler.io;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SimpleServletOutputStream extends ServletOutputStream {

    private boolean headersAreWritten = false;
    @Getter
    @Setter
    private boolean isCompleted = false;
    private final BufferedOutputStream output;
    private final ByteArrayOutputStream outHeaderBuffer = new ByteArrayOutputStream();
    private final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();

    public SimpleServletOutputStream(BufferedOutputStream out) {
        this.output = out;
    }

    @Override
    public void write(int b) {
        if (isCompleted && !headersAreWritten) {
            outHeaderBuffer.write(b);
        } else {
            outBuffer.write(b);
        }
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
    }

    @Override
    public void flush() throws IOException {
        if (!isCompleted){
            return;
        }
        output.write(outHeaderBuffer.toByteArray());
        output.write(outBuffer.toByteArray());
        output.flush();
        outHeaderBuffer.reset();
        outBuffer.reset();
    }

    public void setHeadersAreWritten(boolean headersAreWritten) {
        if(this.headersAreWritten){
            return;
        }
        this.headersAreWritten = headersAreWritten;
    }
    public void reset(){
        isCompleted = false;
        headersAreWritten = false;

    }
}
