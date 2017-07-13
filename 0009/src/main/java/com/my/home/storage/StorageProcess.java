package com.my.home.storage;

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
                    System.out.println("Find in line:\n" + line + "\n regexp: " + pattern.pattern() + "\n result: " + result);
                    if(result)
                    {
                        System.out.println("Set connected");
                        isConnected = true;
                    }
                }
                else
                {
                    System.out.println(line);
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
