package edu.ted.servlethandler.entity;

import lombok.Getter;

import javax.servlet.Servlet;
import java.util.Objects;


public class ServletMapping {
    @Getter
    private final Servlet servlet;
    @Getter
    private final String mapping;

    public ServletMapping(Servlet servlet, String mapping) {
        this.servlet = servlet;
        this.mapping = mapping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServletMapping that = (ServletMapping) o;
        return servlet.equals(that.servlet) &&
                mapping.equals(that.mapping);
    }

    @Override
    public int hashCode() {
        return Objects.hash(servlet, mapping);
    }
}
