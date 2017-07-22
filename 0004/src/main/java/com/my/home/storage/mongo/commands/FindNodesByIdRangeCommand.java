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

    private List<LogIdRange> ranges;
    public FindNodesByIdRangeCommand(List<LogIdRange> ranges)
    {

        Collections.sort(ranges, (o1, o2) -> o1.getFirstId().compareTo(o2.getFirstId()));

        //this.ranges = ranges;
        this.ranges = spliceRanges(ranges);
    }
    @Override
    public String getSelector()
    {

        /**
         * TODO implement request to find by range
         * TODO example:
         * TODO: { $or:[ {$and:[ {"id":{$gte:20}}, {"id":{$lte:23}}]}, {$and:[ {"id":{$gte:30}}, {"id":{$lte:34}}]} ]}
         */
        List<String> cases = getListCommand();
        StringBuilder builder = new StringBuilder();
        builder.append("{$or : [");
        builder.append(split(cases));
        builder.append("]}");
        return builder.toString();
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

    private List<String> getListCommand()
    {
        List<String> out = new ArrayList<>();
        ranges.forEach(range -> out.add((range.getFirstId().equals(range.getLastId()))?
                String.format(EXACT_RANGE, range.getFirstId()):
                String.format(BETWEEN_RANGE, range.getFirstId(), range.getLastId())
        ));
        return out;
    }

    private List<LogIdRange> spliceRanges(List<LogIdRange> ranges)
    {
        List<LogIdRange> out = new ArrayList<>();
        LogIdRange range = new LogIdRange();
        range.setFirstId(ranges.get(0).getFirstId());
        range.setLastId(ranges.get(0).getLastId());
        for (int i = 1; i < ranges.size(); i++)
        {
            if(ranges.get(i).getFirstId() - range.getLastId() == 1)
            {
                range.setLastId(ranges.get(i).getLastId());
            }
            else
            {
                out.add(range);
                range = new LogIdRange();
                range.setFirstId(ranges.get(i).getFirstId());
                range.setLastId(ranges.get(i).getLastId());
            }
        }
        out.add(range);
        return out;
    }
}
