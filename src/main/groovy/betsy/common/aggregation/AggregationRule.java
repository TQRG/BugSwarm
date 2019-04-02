package betsy.common.aggregation;

import java.util.List;

public interface AggregationRule<I, O> {

    O aggregate(List<I> values);

}
