package com.my.home.plugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * Class loader for plugins
 */
public class PluginClassLoader extends URLClassLoader
{
    private static final String[] AVAILABLE_CLASSES = new String[]
    {
        "com.my.home.log.beans",
        "com.my.home.plugin.IPlugin"
    };
    private ClassLoader parent;
    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls);
        this.parent = parent;
    }

    public PluginClassLoader(URL[] urls) {
        super(urls);
    }

    public PluginClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, null, factory);
        this.parent = parent;
    }

    public void addJar(String filePath)
    {
        try {
            if(!isJarLoaded(filePath))
            {
                String urlPath = "file:" + filePath;
                addURL(new URL(urlPath));
            }
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException("Cant load url " + filePath, e);
        }

    }

    public boolean isJarLoaded(String filePath) throws MalformedURLException
    {
        String urlPath = "file:" + filePath;
        URL url = new URL(urlPath);
        URL[] urls = getURLs();
        for (URL currentUrl : urls)
        {
            if (currentUrl.equals(url))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        Class<?> out = null;
        if(validateClassName(name))
        {
            out = parent.loadClass(name);
        }
        else
        {
            out = super.loadClass(name);
        }
        return out;
    }

    private boolean validateClassName(String name)
    {
        boolean out = false;
        for (String str : AVAILABLE_CLASSES)
        {
            out |= name.startsWith(str);
        }
        return out;
    }
}
