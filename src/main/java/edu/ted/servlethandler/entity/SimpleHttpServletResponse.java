package edu.ted.servlethandler.entity;

import edu.ted.servlethandler.entity.adapter.SimpleHttpServletResponseAdapter;
import edu.ted.servlethandler.io.SimpleServletOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SimpleHttpServletResponse extends SimpleHttpServletResponseAdapter {

    private ServletOutputStream output;
    private PrintWriter writer;
    private Map<String, String> headers = new TreeMap<>();


    public SimpleHttpServletResponse() {
    }

    public SimpleHttpServletResponse(ServletOutputStream output) {
        this.output = output;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return output;
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
        //outpu
        output.flush();
    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public void setContentType(String type) {
        setHeader("Content-Type", type);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return Arrays.asList(headers.get(name));
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }



    @Override
    public void reset() {
        setStatus(HttpServletResponse.SC_OK);
        headers.clear();
        ((SimpleServletOutputStream)output).reset();
    }


}
