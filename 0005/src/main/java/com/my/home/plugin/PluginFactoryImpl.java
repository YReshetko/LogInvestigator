package com.my.home.plugin;

import com.my.home.plugin.model.Plugin;
import com.my.home.plugin.model.PluginDescription;
import com.my.home.plugin.model.PluginToStore;
import com.my.home.plugin.model.PluginType;
import com.my.home.util.HashGenerator;
import com.my.home.util.JsonUtils;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class PluginFactoryImpl implements IPluginFactory
{

    private static final String RESOURCE_TEMPLATE = "jar:file:%s!/descriptor.json";
    private IPluginStorage pluginStorage;
    private String pluginsDir;
    private Map<String, PluginClassLoader> loaders;

    public PluginFactoryImpl(IPluginStorage pluginStorage, String pluginsDir) {
        this.pluginStorage = pluginStorage;
        this.pluginsDir = pluginsDir;
        loaders = new HashMap<>();
    }
    @Override
    public List<PluginToStore> savePlugin(File file)
    {
        try {
            File dir = new File(pluginsDir);
            if (dir.isDirectory() && !containsFile(dir, file))
            {
                file = copyPluginToDir(dir, file);
            }
            else
            {
                //file = new File(dir.getAbsoluteFile() + "/" + file.getName());
                file = copyPluginToDir(dir, file);
            }
            Plugin pluginDescription = getResourceFromJar(file);
            List<PluginDescription> descriptions =  pluginDescription.getPlugins();
            if (descriptions == null || descriptions.size() == 0)
            {
                file.delete();
                throw new RuntimeException("Jar archive doesn't contain plugins");
            }
            parsePlugins(descriptions, file);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cant save plugin", e);
        }

        return null;
    }

    @Override
    public void updatePlugins() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <V> V getPlugin(PluginToStore pluginToStore)
    {
        String classToLoad = pluginToStore.getPlugin().getPackageName() + "." + pluginToStore.getPlugin().getClassName();
        try
        {
            Class<V> clazz = (Class<V>) getLoader(pluginToStore.getJar()).loadClass(classToLoad);
            V instance = clazz.newInstance();
            return instance;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Can't load class " + classToLoad, e);
        }

    }

    private void parsePlugins(List<PluginDescription> descriptions, File jar) throws ClassNotFoundException
    {
        String fileName = jar.getAbsolutePath();
        PluginClassLoader loader = getLoader(fileName);
        for (PluginDescription description : descriptions)
        {
            boolean result = false;
            result |= tryToSavePlugin(IPluginSelector.class, PluginType.SELECTOR, fileName, loader, description);
            result |= tryToSavePlugin(IPluginFilter.class, PluginType.FILTER, fileName, loader, description);
            result |= tryToSavePlugin(IPluginProcessor.class, PluginType.PROCESSOR, fileName, loader, description);
            result |= tryToSavePlugin(IPluginPostProcessor.class, PluginType.POST_PROCESSOR, fileName, loader, description);
            if (!result)
            {
                throw new ClassNotFoundException("The class " + description.getClassName() + " which was descripted in jar "
                        + jar.getName() + " is not a plugin of any type");
            }

        }
    }
    private <V> boolean tryToSavePlugin(Class<V> vClass, PluginType pluginType, String fileName, PluginClassLoader loader, PluginDescription description) throws ClassNotFoundException
    {
        Class clazz = loader.loadClass(description.getPackageName() + "." + description.getClassName());
        if (isInstance(clazz, vClass))
        {
            String id = getId(description, pluginType.value().toLowerCase(), fileName);
            PluginToStore toStore = new PluginToStore();
            toStore.setId(id);
            toStore.setJar(fileName);
            toStore.setPlugin(description);
            toStore.setStorageRef("default");
            toStore.setType(pluginType);
            pluginStorage.save(toStore);
            return true;
        }
        else
        {
            return false;
        }
    }
    private boolean isInstance(Class toCheck, Class forCheck)
    {
        Class[] interfaces = toCheck.getInterfaces();
        for (Class cls : interfaces)
        {
            if (cls.equals(forCheck))
            {
                return true;
            }
        }
        return false;
    }

    private String getId(PluginDescription description, String type, String file)
    {
        List<String> stringsToHash = new ArrayList<>();
        stringsToHash.add(file);
        stringsToHash.add(type);
        stringsToHash.add(description.getLabel());
        stringsToHash.add(description.getPackageName());
        stringsToHash.add(description.getClassName());
        return HashGenerator.hash(stringsToHash);
    }

    private Plugin getResourceFromJar(File file)
    {
        try
        {
            String pathToResource = String.format(RESOURCE_TEMPLATE, file.getAbsoluteFile());
            URL url = new URL(pathToResource);
            InputStream inputStream = url.openStream();
            String result = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));
            return JsonUtils.getObject(result, Plugin.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Can't read resource from plugin", e);
        }
    }
    private boolean containsFile(File dir, File file)
    {
        File[] files = dir.listFiles();
        String fileName = file.getName();
        for (File currentFile : files)
        {
            if (currentFile.isFile() && currentFile.getName().equals(fileName))
            {
                return true;
            }
        }
        return false;
    }
    private File copyPluginToDir(File dir, File file) throws IOException
    {
        File dest = new File(dir.getAbsoluteFile() + "/" + file.getName());
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(file).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
        finally
        {
            sourceChannel.close();
            destChannel.close();
            return dest;
        }
    }

    private PluginClassLoader getLoader(String jar)
    {
        PluginClassLoader loader = loaders.get(jar);
        if(loader == null)
        {
            URL[] url = {};
            loader = new PluginClassLoader(url, this.getClass().getClassLoader());
            loader.addJar(jar);
            loaders.put(jar, loader);
        }
        return loader;
    }
}
