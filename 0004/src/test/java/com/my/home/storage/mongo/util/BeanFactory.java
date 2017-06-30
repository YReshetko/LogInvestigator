package com.my.home.storage.mongo.util;

import com.my.home.log.beans.LogNode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BeanFactory
{
    public static LogNode getLogNode()
    {
        LogNode out = new LogNode();
        long id = Math.round(Math.random() * 1000);
        out.setId(id);
        out.setThread("TestThread-" + String.valueOf(id));
        out.setLongDateTime(id * 1000);
        out.setDate("Date - " + String.valueOf(id));
        out.setIsStamped(true);
        out.setMessage("Some test message - " + String.valueOf(id));
        out.setClassPackage("test.class.package - " + String.valueOf(id));
        out.setLogLevel("TEST_LOG_LEVEL - " + String.valueOf(id));
        out.setTime("Time - " + String.valueOf(id));
        return out;
    }
    public static List<LogNode> getLogNodes(int number)
    {
        List<LogNode> out = new ArrayList<>(number);

        for (int i = 0; i<number; i++)
        {
            out.add(getLogNode());
        }
        return out;
    }
}
