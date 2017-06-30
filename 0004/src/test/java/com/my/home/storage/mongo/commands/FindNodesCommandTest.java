package com.my.home.storage.mongo.commands;

import com.my.home.log.beans.LogNode;
import com.my.home.storage.mongo.util.BeanFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test command to find some list of objects
 */
public class FindNodesCommandTest
{
    private FindNodesCommand m_toTest;

    @Before
    public void setUp()
    {
        m_toTest = new FindNodesCommand();
    }
    @Test
    public void testPreparingRequest()
    {
        LogNode node = BeanFactory.getLogNode();
        m_toTest.setData(node);
        String request = m_toTest.getCommand();
        Assert.assertTrue(request.contains(node.getClassPackage()));
        Assert.assertTrue(request.contains(node.getDate()));
        Assert.assertTrue(request.contains(node.getLogLevel()));
        Assert.assertTrue(request.contains(node.getMessage()));
        Assert.assertTrue(request.contains(node.getThread()));
        Assert.assertTrue(request.contains(node.getTime()));
        Assert.assertTrue(request.contains(String.valueOf(node.getLongDateTime())));
        Assert.assertTrue(request.contains("$or"));
    }
}
