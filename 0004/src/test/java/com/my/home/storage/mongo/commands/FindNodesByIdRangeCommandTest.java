package com.my.home.storage.mongo.commands;

import com.my.home.log.beans.LogIdRange;
import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Yurchik
 * Date: 04.01.18
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class FindNodesByIdRangeCommandTest
{
    private static final String RANGES = "1,3,5,10-15,23,25,30-45,48-120";

    private FindNodesByIdRangeCommand toTest;

    @Test
    public void testRanges()
    {
        toTest = new FindNodesByIdRangeCommand(getRanges(), 10);
        List<String> selectors = toTest.getSelectors();
        System.out.println(selectors);
        Assert.assertEquals(3, selectors.size());
    }

    private List<LogIdRange> getRanges()
    {
        List<LogIdRange> out = new ArrayList<>();

        String[] ranges = RANGES.split(",");
        for (String str : ranges)
        {
            LogIdRange range = new LogIdRange();
            if(str.indexOf('-') == -1)
            {
                range.setFirstId(Long.valueOf(str));
                range.setLastId(Long.valueOf(str));
            }
            else
            {
                String[] firstLast = str.split("-");
                range.setFirstId(Long.valueOf(firstLast[0]));
                range.setLastId(Long.valueOf(firstLast[1]));
            }
            out.add(range);
        }

        return out;
    }
}
