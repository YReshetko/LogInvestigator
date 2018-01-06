package com.my.home.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class StorageProcess extends Thread
{
    private final static Logger logger = LogManager.getLogger(StorageProcess.class.getName());
    private static final String DETECT_IF_CONNECTED = "waiting for connections on port";
    private String command;
    private Process dbProcess;
    private boolean isConnected;
    private boolean isInterrupted;
    public StorageProcess(String command)
    {
        this.command = command;
        isConnected = false;
        isInterrupted = false;
        setDaemon(true);
    }
    @Override
    public void run()
    {
        try
        {
            Runtime runtime = Runtime.getRuntime();
            dbProcess = runtime.exec(command);
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(dbProcess.getInputStream()));
            Pattern pattern = Pattern.compile(DETECT_IF_CONNECTED);

            while ((line = reader.readLine()) != null)
            {

                if(!isConnected /*&& line.matches(DETECT_IF_CONNECTED)*/ /*line.contains(DETECT_IF_CONNECTED)*/)
                {

                    Matcher matcher = pattern.matcher(line);
                    boolean result = matcher.find();
                    logger.debug("Find in line:\n" + line + "\n regexp: " + pattern.pattern() + "\n result: " + result);
                    if(result)
                    {
                        logger.debug("Set connected");
                        isConnected = true;
                    }
                }
                else
                {
                    logger.debug(line);
                }
            }
            logger.debug("Interrupted DB process: " + dbProcess.toString());
            isInterrupted = true;
        }
        catch (IOException e)
        {
            close();
            isInterrupted = true;
            logger.error("Can not run mongo process", e);
        }
    }
    public void close()
    {
        if( dbProcess != null)
        {
            dbProcess.destroy();
        }
    }

    public boolean isConnected()
    {
        return this.isConnected;
    }
    public boolean checkIfInterrupted()
    {
        return isInterrupted;
    }
}
