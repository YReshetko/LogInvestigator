package com.my.home.base;

import junit.framework.TestCase;
import org.junit.Assert;

import java.io.*;

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
    protected String loadFile(String path) throws IOException
    {
        if(System.getProperty("basedir") == null)
        {
            path = "0003/" + MODULE_PATH + path;
        }
        else
        {
            path = System.getProperty("basedir")+ "/" + MODULE_PATH + path;
        }
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
}
