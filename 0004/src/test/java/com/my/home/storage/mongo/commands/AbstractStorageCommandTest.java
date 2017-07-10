package com.my.home.storage.mongo.commands;

import com.my.home.log.beans.LogNode;
import com.my.home.processor.ILogStorageCommand;
import com.my.home.storage.mongo.util.BeanFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *  Test abstract storage command
 */
public class AbstractStorageCommandTest
{
    private AbstractStorageCommand<LogNode> m_toTest;

    /**
     * Setup test
     */
    @Before
    public void setUp()
    {
        m_toTest = new AbstractStorageCommandTestImpl<>();
    }

    /**
     * Test of saving nodes for command
     */
    @Test
    public void testSetData()
    {
        LogNode node = BeanFactory.getLogNode();
        try {
            m_toTest.setData(node, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {

        }
        try {
            m_toTest.setData();
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {

        }

        try {
            m_toTest.setData(node, null, node);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    /**
     * Test implementation of abstract class
     * @param <V>
     */
    private class AbstractStorageCommandTestImpl<V> extends AbstractStorageCommand<V>
    {
        @Override
        public String getSelector() {
            throw new UnsupportedOperationException("This method is out of testing");
        }

        @Override
        public Class<V> getType() {
            return null;
        }

        @Override
        public Command getCommandType() {
            return null;
        }
    }
}
