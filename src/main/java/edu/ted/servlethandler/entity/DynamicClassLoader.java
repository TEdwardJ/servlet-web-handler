package edu.ted.servlethandler.entity;

import java.net.URL;
import java.net.URLClassLoader;


public class DynamicClassLoader extends URLClassLoader {

    private final URL[] classPath;

    public DynamicClassLoader(URL[] classPath, ClassLoader parent) {
        super(classPath, parent);
        this.classPath = classPath;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                try {
                    c = super.findClass(name);
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }
            }
            if (c == null) {
                // If still not found, then invoke parent findClass in order
                // to find the class.
                try {
                    if (getParent() != null) {
                        c = getParent().loadClass(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
}
