package com.my.home.base;

import junit.framework.TestCase;
import org.junit.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Yurchik
 * Date: 23.06.17
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
public class TestBase
{
    private static final String MODULE_PATH = "src/test/resources/";
    protected List<File> getFiles(List<String> fileNames)
    {
        List<File> files = new ArrayList<>(fileNames.size());
        String basePath = getBasePath();
        for (String fileName : fileNames)
        {
            files.add(new File(basePath + fileName));
        }
        return files;
    }
    protected String loadFile(String path) throws IOException
    {
        path = getBasePath() + path;
        File file = new File(path);
        System.out.println(file.getAbsolutePath());
        Assert.assertTrue(file.exists());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine())!= null)
        {
            builder.append(line);
        }
        return builder.toString();
    }

    private String getBasePath()
    {
        String out;
        if(System.getProperty("basedir") == null)
        {
            out = "0003/" + MODULE_PATH;
        }
        else
        {
            out = System.getProperty("basedir")+ "/" + MODULE_PATH;
        }
        return out;
    }
}
