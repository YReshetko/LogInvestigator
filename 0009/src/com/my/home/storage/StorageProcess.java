package com.my.home.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 */
public class StorageProcess extends Thread
{

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
            while ((line = reader.readLine()) != null)
            {
                System.out.println(line);
                if(line.contains(DETECT_IF_CONNECTED))
                {
                    isConnected = true;
                }
            }
            System.out.println(dbProcess);
            isInterrupted = true;
        }
        catch (IOException e)
        {
            close();
            isInterrupted = true;
            e.printStackTrace();
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
