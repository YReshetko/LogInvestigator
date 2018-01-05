package com.my.home.storage.mongo.commands;

import com.my.home.log.beans.LogIdRange;
import com.my.home.log.beans.LogNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class FindNodesByIdRangeCommand extends AbstractStorageCommand<LogNode>
{
    private static final String BETWEEN_RANGE = "{$and:[ {\"id\" : {$gte : %s}}, {\"id\" : {$lte : %s}}]}";
    private static final String EXACT_RANGE = "{\"id\" : %s}";

    private List<List<LogIdRange>> ranges;
    private int rangeSize;
    private long size;
    public FindNodesByIdRangeCommand(List<LogIdRange> ranges, int rangeSize)
    {

        Collections.sort(ranges, (o1, o2) -> o1.getFirstId().compareTo(o2.getFirstId()));
        this.size = 0L;
        //this.ranges = ranges;
        this.ranges = spliceRanges(ranges);
        this.rangeSize = rangeSize;

    }

    /**
     * @return
     */
    @Override
    public List<String> getSelectors()
    {

        /**
         * TODO implement request to find by range
         * TODO example:
         * TODO: { $or:[ {$and:[ {"id":{$gte:20}}, {"id":{$lte:23}}]}, {$and:[ {"id":{$gte:30}}, {"id":{$lte:34}}]} ]}
         */
        List<String> out = new ArrayList<>(ranges.size());
        for (List<LogIdRange> listRanges : ranges)
        {
            List<String> cases = getListCommand(listRanges);
            StringBuilder builder = new StringBuilder();
            builder.append("{$or : [");
            builder.append(split(cases));
            builder.append("]}");
            out.add(builder.toString());
        }
        return out;
    }

    @Override
    public Class<LogNode> getType() {
        return LogNode.class;
    }

    @Override
    public Command getCommandType() {
        return Command.FIND;
    }
    @Override
    public String sortBy() {
        return "id";
    }
    @Override
    public long getSize(){
        return this.size;
    }



    private List<String> getListCommand(List<LogIdRange> ranges)
    {
        List<String> out = new ArrayList<>();
        ranges.forEach(range -> out.add((range.getFirstId().equals(range.getLastId()))?
                String.format(EXACT_RANGE, range.getFirstId()):
                String.format(BETWEEN_RANGE, range.getFirstId(), range.getLastId())
        ));
        return out;
    }

    private List<List<LogIdRange>> spliceRanges(List<LogIdRange> ranges)
    {
        List<LogIdRange> rangesToUse = new ArrayList<>();
        LogIdRange range = new LogIdRange();
        range.setFirstId(ranges.get(0).getFirstId());
        range.setLastId(ranges.get(0).getLastId());
        for (int i = 1; i < ranges.size(); i++)
        {
            if(ranges.get(i).getFirstId() - range.getLastId() == 1 && range.getLastId() - range.getFirstId() < rangeSize)
            {
                range.setLastId(ranges.get(i).getLastId());
            }
            else
            {
                rangesToUse.add(range);
                range = new LogIdRange();
                range.setFirstId(ranges.get(i).getFirstId());
                range.setLastId(ranges.get(i).getLastId());
            }
        }
        rangesToUse.add(range);

        List<List<LogIdRange>> out = new ArrayList<>();
        int currentSize = 0;
        List<LogIdRange> listRanges = new ArrayList<>();

        for (LogIdRange range1 : rangesToUse)
        {
            long diff = range1.getLastId() - range1.getFirstId() + 1;
            currentSize += diff;
            this.size += diff;
            if (currentSize > rangeSize)
            {
                listRanges.add(range1);
                out.add(listRanges);
                currentSize = 0;
                listRanges = new ArrayList<>();
            }
            else
            {
                listRanges.add(range1);
            }
        }

        return out;
    }
}
