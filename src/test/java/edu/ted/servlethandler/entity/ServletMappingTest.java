package edu.ted.servlethandler.entity;

import edu.ted.servlethandler.entity.ServletMapping;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ServletMappingTest {

    @Test
    public void test(){
        HttpServlet servlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                super.doGet(req, resp);
            }
        };
        ServletMapping servletMapping0 = new ServletMapping(servlet, "/map1");
        ServletMapping servletMapping1 = new ServletMapping(servlet, "/map1");
        ServletMapping servletMapping2 = new ServletMapping(servlet, "/map2");
        assertEquals("/map1", servletMapping0.getMapping());
        assertEquals("/map1", servletMapping1.getMapping());
        assertEquals("/map2", servletMapping2.getMapping());
        assertEquals(servletMapping0, servletMapping1);
        assertNotEquals(servletMapping2, servletMapping1);
    }

}