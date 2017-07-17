package com.my.home.storage.mongo.commands;

import com.my.home.log.beans.LogIdRange;
import com.my.home.log.beans.LogNode;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class FindNodesByIdRangeCommand extends AbstractStorageCommand<LogNode>
{

    private List<LogIdRange> ranges;
    public FindNodesByIdRangeCommand(List<LogIdRange> ranges)
    {
        this.ranges = ranges;
        Collections.sort(ranges, (o1, o2) -> o1.getFirstId().compareTo(o2.getFirstId()));

    }
    @Override
    public String getSelector()
    {

        /**
         * TODO implement request to find by range
         * TODO example:
         * TODO: { $or:[ {$and:[ {"id":{$gte:20}}, {"id":{$lte:23}}]}, {$and:[ {"id":{$gte:30}}, {"id":{$lte:34}}]} ]}
         */
        /*List<String> cases = getListCommand(value);
        StringBuilder builder = new StringBuilder();
        builder.append("{$or : [");
        builder.append(split(cases));
        builder.append("]}");
        return builder.toString();*/
        return null;
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
}
