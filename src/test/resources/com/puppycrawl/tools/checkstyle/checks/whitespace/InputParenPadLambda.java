package com.puppycrawl.tools.checkstyle.checks.whitespace;

class InputParenPadLambda
{
    {
        java.util.function.Consumer c = ( o ) -> { o.toString(); }; // 2 violations

        java.util.stream.Stream.of().forEach(( o ) -> o.toString()); // 2 violations

        java.util.stream.Stream.of().forEach(( Object o ) -> o.toString()); // 2 violations

        java.util.stream.Stream.of().forEach(o -> o.toString( )); // 2 violations
    }

    void someMethod( String param ) // 2 violations
    {
    }
}
