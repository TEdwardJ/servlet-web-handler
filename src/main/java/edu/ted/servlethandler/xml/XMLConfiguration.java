package edu.ted.servlethandler.xml;

import edu.ted.servlethandler.ServletDefinition;
import edu.ted.servlethandler.exception.XMLConfigurationCreationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class XMLConfiguration {

    private final String webConfigPath;
    private URL webConfigSource;
    private SAXParser parser;
    private XMLConfiguration.WebXmlHandler webXmlHandler;
    private final ClassLoader classLoader;

    @Getter
    private final Map<String, ServletDefinition> servletDefinitions = new HashMap<>();

    public XMLConfiguration(String webConfigPath, ClassLoader classLoader) {
        this.webConfigPath = webConfigPath;
        this.classLoader = classLoader;
    }

    public void init() throws FileNotFoundException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            log.error("Attempt to create parser for XML configuration is failed", e);
        }
        webXmlHandler = new XMLConfiguration.WebXmlHandler();
        webConfigSource = classLoader.getResource(webConfigPath);
        if (webConfigSource == null) {
            throw new FileNotFoundException("Config File " + webConfigPath + " not found");
        }

    }

    public void parse() throws XMLConfigurationCreationException {
        try {
            final URI configurationURI = webConfigSource.toURI();
            parser.parse(new File(configurationURI), webXmlHandler);
        } catch (IOException | SAXException e) {
            log.error("Some Exception during XML Configuration reading", e);
            throw new XMLConfigurationCreationException(e);
        } catch (URISyntaxException e) {
            log.error("Bad URL {}", webConfigSource, e);
            throw new XMLConfigurationCreationException(e);
        }
    }

    public class WebXmlHandler extends DefaultHandler {

        private static final String SERVLET = "servlet";
        private static final String SERVLET_MAPPING = "servlet-mapping";
        private static final String SERVLET_NAME = "servlet-name";
        private static final String SERVLET_CLASS = "servlet-class";
        private static final String URL_PATTERN = "url-pattern";
        private static final String INIT_PARAM = "init-param";
        private static final String PARAM_NAME = "param-name";
        private static final String PARAM_VALUE = "param-value";

        private String elementValue;
        private String servletName;
        private String servletClass;
        private String urlPattern;
        private String paramName;
        private String paramValue;
        private Map<String, String> parametersMap;
        private ServletDefinition currentServletDefinition;

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            log.debug("TAG {} End: Value={}", qName, elementValue);
            switch (qName) {
                case SERVLET:
                    currentServletDefinition.setAlias(servletName);
                    currentServletDefinition.setClassIdentifier(servletClass);
                    currentServletDefinition.setParameters(parametersMap);
                    XMLConfiguration.this.servletDefinitions.put(servletName, currentServletDefinition);
                    break;
                case SERVLET_MAPPING:
                    XMLConfiguration.this.servletDefinitions.get(servletName).addMapping(urlPattern);
                    break;
                case SERVLET_NAME:
                    servletName = elementValue;
                    break;
                case SERVLET_CLASS:
                    servletClass = elementValue;
                    break;
                case URL_PATTERN:
                    urlPattern = elementValue;
                    break;
                case PARAM_NAME:
                    paramName = elementValue;
                    break;
                case PARAM_VALUE:
                    paramValue = elementValue;
                    break;
                case INIT_PARAM:
                    parametersMap.put(paramName, paramValue);
                    break;
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            log.debug("TAG Start: {}", qName);
            switch (qName) {
                case SERVLET:
                    currentServletDefinition = new ServletDefinition();
                    parametersMap = new HashMap<>();
                    break;
            }
            getTagAttributes(attributes);
        }

        private void getTagAttributes(Attributes attributes) {
            int counter = 0;
            for (int i = 0; i < attributes.getLength(); i++) {
                log.debug("Attr: {} {}", attributes.getValue(attributes.getLocalName(i)), attributes.getLocalName(i));
                counter++;
            }
            if (counter == 0) {
                log.debug("No Attributes");
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            elementValue = new String(ch, start, length);
        }
    }


}
