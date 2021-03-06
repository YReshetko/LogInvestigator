package com.my.home.storage;

import com.my.home.BaseLogger;
import com.my.home.storage.mongo.impl.MongoConnection;

import java.io.*;
import java.util.Properties;

/**
 *
 */
public class MongoStorageManager extends BaseLogger
{

    private static final String COMMAND_TO_RUN_DB = "\"%s/mongod.exe\" --dbpath \"%s\"";
    private File propFile;
    private Properties properties;
    private StorageProcess dbProcess;
    public MongoStorageManager(String path)
    {
        try
        {
            File propFile = new File(path);
            if(!propFile.exists())
            {
                throw new IOException("File with mongo properties is not exist: " + propFile.getAbsolutePath());
            }
            Properties properties = new Properties();
            properties.load(new FileReader(propFile));
            this.propFile = propFile;
            this.properties = properties;
        }
        catch (IOException e)
        {
            error("Error loading mongo properties from file " + path, e);
        }
    }

    public Properties getProps()
    {
        return properties;
    }

    public boolean needAutoRunDB()
    {
       return Boolean.valueOf(properties.getProperty("storage.mongo.openAtStart"));
    }
    public void changeProperties(Properties properties)
    {
        if (properties == null)
        {
            throw new IllegalArgumentException("The mongo properties should exist");
        }
        try
        {
            properties.store(new FileWriter(propFile), "Saving new Properties");
            this.properties = properties;
        }
        catch (IOException e)
        {
            error("Error saving mongo properties", e);
        }
    }
    public MongoConnection getConnection()
    {
        MongoConnection conn = new MongoConnection();
        conn.setDbName(properties.getProperty("storage.mongo.db"));
        conn.setHost(properties.getProperty("storage.mongo.host"));
        conn.setPort(Integer.parseInt(properties.getProperty("storage.mongo.port")));
        conn.init();
        return conn;
    }

    public void runDB()
    {

        if (dbProcess != null)
        {
            dbProcess.close();
            dbProcess = null;
        }
        dbProcess = new StorageProcess(
                String.format(COMMAND_TO_RUN_DB,
                        properties.getProperty("storage.mongo.path"),
                        properties.getProperty("storage.mongo.db.path")));
        dbProcess.start();
        while (true)
        {
            boolean isConnected = dbProcess.isConnected();
            if (isConnected)
            {
                log("MONGO CONNECTED");
                break;
            }
            if(dbProcess.checkIfInterrupted())
            {
                throw new RuntimeException("Can't create connection");
            }
        }
        log("FINISH STORAGE INITIALIZATION");
    }
    public void closeDB()
    {
        if(dbProcess != null)
        {
            dbProcess.close();
            dbProcess = null;
        }
    }

}
