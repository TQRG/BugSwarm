package org.fluentlenium.core.filter.matcher;


import java.util.regex.Pattern;

//TODO Remove matching pattern from there
public class ContainsMatcher extends Matcher {

    public ContainsMatcher(String value) {
        super(value);
    }

    public ContainsMatcher(Pattern value) {
        super(value);
    }

    @Override
    public MatcherType getMatcherType() {
        return MatcherType.CONTAINS;
    }

    @Override
    public boolean isSatisfiedBy(String o) {
        return CalculateService.contains(getPattern(), getValue(), o);
    }


}
