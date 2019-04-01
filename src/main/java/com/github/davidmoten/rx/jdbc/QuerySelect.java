package com.github.davidmoten.rx.jdbc;

import static com.github.davidmoten.rx.jdbc.Conditions.checkNotNull;
import static com.github.davidmoten.rx.jdbc.Queries.bufferedParameters;

import java.sql.ResultSet;
import java.util.List;

import rx.Observable;
import rx.Observable.Operator;
import rx.functions.Func1;

import com.github.davidmoten.rx.jdbc.tuple.Tuple2;
import com.github.davidmoten.rx.jdbc.tuple.Tuple3;
import com.github.davidmoten.rx.jdbc.tuple.Tuple4;
import com.github.davidmoten.rx.jdbc.tuple.Tuple5;
import com.github.davidmoten.rx.jdbc.tuple.Tuple6;
import com.github.davidmoten.rx.jdbc.tuple.Tuple7;
import com.github.davidmoten.rx.jdbc.tuple.TupleN;
import com.github.davidmoten.rx.jdbc.tuple.Tuples;

/**
 * A query and its executable context.
 */
final public class QuerySelect implements Query {

    private final String sql;
    private final Observable<Parameter> parameters;
    private final QueryContext context;
    private Observable<?> depends = Observable.empty();

    /**
     * Constructor.
     * 
     * @param sql
     * @param parameters
     * @param depends
     * @param context
     */
    private QuerySelect(String sql, Observable<Parameter> parameters, Observable<?> depends,
            QueryContext context) {
        checkNotNull(sql);
        checkNotNull(parameters);
        checkNotNull(depends);
        checkNotNull(context);
        this.sql = sql;
        this.parameters = parameters;
        this.depends = depends;
        this.context = context;
    }

    @Override
    public String sql() {
        return sql;
    }

    @Override
    public QueryContext context() {
        return context;
    }

    @Override
    public Observable<Parameter> parameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "QuerySelect [sql=" + sql + "]";
    }

    @Override
    public Observable<?> depends() {
        return depends;
    }

    /**
     * Returns the results of running a select query with all sets of
     * parameters.
     * 
     * @return
     */
    public <T> Observable<T> execute(ResultSetMapper<? extends T> function) {
        return bufferedParameters(this)
        // execute once per set of parameters
                .concatMap(executeOnce(function));
    }

    /**
     * Returns a {@link Func1} that itself returns the results of pushing one
     * set of parameters through a select query.
     * 
     * @param query
     * @return
     */
    private <T> Func1<List<Parameter>, Observable<T>> executeOnce(
            final ResultSetMapper<? extends T> function) {
        return new Func1<List<Parameter>, Observable<T>>() {
            @Override
            public Observable<T> call(List<Parameter> params) {
                return executeOnce(params, function);
            }
        };
    }

    /**
     * Returns an Observable of the results of pushing one set of parameters
     * through a select query.
     * 
     * @param params
     *            one set of parameters to be run with the query
     * @return
     */
    private <T> Observable<T> executeOnce(final List<Parameter> params,
            ResultSetMapper<? extends T> function) {
        return QuerySelectOperation.execute(this, params, function)
                .subscribeOn(context.scheduler());
    }

    /**
     * Builds a {@link QuerySelect}.
     */
    final public static class Builder {

        /**
         * Builds the standard stuff.
         */
        private final QueryBuilder builder;

        /**
         * Constructor.
         * 
         * @param sql
         * @param db
         */
        public Builder(String sql, Database db) {
            builder = new QueryBuilder(sql, db);
        }

        /**
         * Appends the given parameters to the parameter list for the query. If
         * there are more parameters than required for one execution of the
         * query then more than one execution of the query will occur.
         * 
         * @param parameters
         * @return this
         */
        public <T> Builder parameters(Observable<T> parameters) {
            builder.parameters(parameters);
            return this;
        }

        /**
         * Appends the given parameter values to the parameter list for the
         * query. If there are more parameters than required for one execution
         * of the query then more than one execution of the query will occur.
         * 
         * @param objects
         * @return this
         */
        public Builder parameters(Object... objects) {
            builder.parameters(objects);
            return this;
        }

        /**
         * Appends a parameter to the parameter list for the query. If there are
         * more parameters than required for one execution of the query then
         * more than one execution of the query will occur.
         * 
         * @param value
         * @return this
         */
        public Builder parameter(Object value) {
            builder.parameter(value);
            return this;
        }

        /**
         * Appends a dependency to the dependencies that have to complete their
         * emitting before the query is executed.
         * 
         * @param dependency
         * @return this
         */
        public Builder dependsOn(Observable<?> dependency) {
            builder.dependsOn(dependency);
            return this;
        }

        /**
         * Appends a dependency on the result of the last transaction (
         * <code>true</code> for commit or <code>false</code> for rollback) to
         * the dependencies that have to complete their emitting before the
         * query is executed.
         * 
         * @return this
         */
        public Builder dependsOnLastTransaction() {
            builder.dependsOnLastTransaction();
            return this;
        }

        /**
         * Transforms the results using the given function.
         * 
         * @param function
         * @return
         */
        public <T> Observable<T> get(ResultSetMapper<? extends T> function) {
            return new QuerySelect(builder.sql(), builder.parameters(), builder.depends(),
                    builder.context()).execute(function);
        }

        /**
         * <p>
         * Transforms each row of the {@link ResultSet} into an instance of
         * <code>T</code> using <i>automapping</i> of the ResultSet columns into
         * corresponding constructor parameters that are assignable. Beyond
         * normal assignable criteria (for example Integer 123 is assignable to
         * a Double) other conversions exist to facilitate the automapping:
         * </p>
         * <p>
         * They are:
         * <ul>
         * <li>java.sql.Blob &#10143; byte[]</li>
         * <li>java.sql.Blob &#10143; java.io.InputStream</li>
         * <li>java.sql.Clob &#10143; String</li>
         * <li>java.sql.Clob &#10143; java.io.Reader</li>
         * <li>java.sql.Date &#10143; java.util.Date</li>
         * <li>java.sql.Date &#10143; Long</li>
         * <li>java.sql.Timestamp &#10143; java.util.Date</li>
         * <li>java.sql.Timestamp &#10143; Long</li>
         * <li>java.sql.Time &#10143; java.util.Date</li>
         * <li>java.sql.Time &#10143; Long</li>
         * <li>java.math.BigInteger &#10143;
         * Short,Integer,Long,Float,Double,BigDecimal</li>
         * <li>java.math.BigDecimal &#10143;
         * Short,Integer,Long,Float,Double,BigInteger</li>
         * </p>
         * 
         * @param cls
         * @return
         */
        public <T> Observable<T> autoMap(Class<T> cls) {
            if (builder.sql() == null) {
                com.github.davidmoten.rx.jdbc.annotations.Query query = cls
                        .getAnnotation(com.github.davidmoten.rx.jdbc.annotations.Query.class);
                if (query != null && query.value() != null) {
                    String sql = query.value();
                    builder.setSql(sql);
                } else
                    throw new RuntimeException(
                            "Class "
                                    + cls
                                    + " must be annotated with @Query(sql) or sql must be specified to the builder.select() call");
            }
            return get(Util.autoMap(cls));
        }

        /**
         * Automaps the first column of the ResultSet into the target class
         * <code>cls</code>.
         * 
         * @param cls
         * @return
         */
        public <S> Observable<S> getAs(Class<S> cls) {
            return get(Tuples.single(cls));
        }

        /**
         * Automaps all the columns of the {@link ResultSet} into the target
         * class <code>cls</code>. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls
         * @return
         */
        public <S> Observable<TupleN<S>> getTupleN(Class<S> cls) {
            return get(Tuples.tupleN(cls));
        }

        /**
         * Automaps all the columns of the {@link ResultSet} into {@link Object}
         * . See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls
         * @return
         */
        public <S> Observable<TupleN<Object>> getTupleN() {
            return get(Tuples.tupleN(Object.class));
        }

        /**
         * Automaps the columns of the {@link ResultSet} into the specified
         * classes. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls1
         * @param cls2
         * @return
         */
        public <T1, T2> Observable<Tuple2<T1, T2>> getAs(Class<T1> cls1, Class<T2> cls2) {
            return get(Tuples.tuple(cls1, cls2));
        }

        /**
         * Automaps the columns of the {@link ResultSet} into the specified
         * classes. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls1
         * @param cls2
         * @param cls3
         * @return
         */
        public <T1, T2, T3> Observable<Tuple3<T1, T2, T3>> getAs(Class<T1> cls1, Class<T2> cls2,
                Class<T3> cls3) {
            return get(Tuples.tuple(cls1, cls2, cls3));
        }

        /**
         * Automaps the columns of the {@link ResultSet} into the specified
         * classes. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls1
         * @param cls2
         * @param cls3
         * @param cls4
         * @return
         */
        public <T1, T2, T3, T4> Observable<Tuple4<T1, T2, T3, T4>> getAs(Class<T1> cls1,
                Class<T2> cls2, Class<T3> cls3, Class<T4> cls4) {
            return get(Tuples.tuple(cls1, cls2, cls3, cls4));
        }

        /**
         * Automaps the columns of the {@link ResultSet} into the specified
         * classes. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls1
         * @param cls2
         * @param cls3
         * @param cls4
         * @param cls5
         * @return
         */
        public <T1, T2, T3, T4, T5> Observable<Tuple5<T1, T2, T3, T4, T5>> getAs(Class<T1> cls1,
                Class<T2> cls2, Class<T3> cls3, Class<T4> cls4, Class<T5> cls5) {
            return get(Tuples.tuple(cls1, cls2, cls3, cls4, cls5));
        }

        /**
         * Automaps the columns of the {@link ResultSet} into the specified
         * classes. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls1
         * @param cls2
         * @param cls3
         * @param cls4
         * @param cls5
         * @param cls6
         * @return
         */
        public <T1, T2, T3, T4, T5, T6> Observable<Tuple6<T1, T2, T3, T4, T5, T6>> getAs(
                Class<T1> cls1, Class<T2> cls2, Class<T3> cls3, Class<T4> cls4, Class<T5> cls5,
                Class<T6> cls6) {
            return get(Tuples.tuple(cls1, cls2, cls3, cls4, cls5, cls6));
        }

        /**
         * Automaps the columns of the {@link ResultSet} into the specified
         * classes. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls1
         * @param cls2
         * @param cls3
         * @param cls4
         * @param cls5
         * @param cls6
         * @param cls7
         * @return
         */
        public <T1, T2, T3, T4, T5, T6, T7> Observable<Tuple7<T1, T2, T3, T4, T5, T6, T7>> getAs(
                Class<T1> cls1, Class<T2> cls2, Class<T3> cls3, Class<T4> cls4, Class<T5> cls5,
                Class<T6> cls6, Class<T7> cls7) {
            return get(Tuples.tuple(cls1, cls2, cls3, cls4, cls5, cls6, cls7));
        }

        public Observable<Integer> count() {
            return get(new ResultSetMapper<Integer>() {
                @Override
                public Integer call(ResultSet rs) {
                    return 1;
                }
            }).count();
        }

        /**
         * Returns an {@link Operator} to allow the query to be pushed
         * parameters via the {@link Observable#lift(Operator)} method.
         * 
         * @return operator that acts on parameters
         */
        public OperatorBuilder<Object> parameterOperator() {
            return new OperatorBuilder<Object>(this, OperatorType.PARAMETER);
        }

        /**
         * Returns an {@link Operator} to allow the query to be pushed
         * dependencies via the {@link Observable#lift(Operator)} method.
         * 
         * @return operator that acts on dependencies
         */
        public OperatorBuilder<Object> dependsOnOperator() {
            return new OperatorBuilder<Object>(this, OperatorType.DEPENDENCY);
        }

        /**
         * Returns an {@link Operator} that runs a select query for each list of
         * parameter objects in the source observable.
         * 
         * @return
         */
        public OperatorBuilder<Observable<Object>> parameterListOperator() {
            return new OperatorBuilder<Observable<Object>>(this, OperatorType.PARAMETER_LIST);
        }

    }

    /**
     * Builder pattern for select query {@link Operator}.
     */
    public static class OperatorBuilder<R> {

        private final Builder builder;
        private final OperatorType operatorType;

        /**
         * Constructor.
         * 
         * @param builder
         * @param operatorType
         */
        public OperatorBuilder(Builder builder, OperatorType operatorType) {
            this.builder = builder;
            this.operatorType = operatorType;
        }

        /**
         * Transforms the results using the given function.
         * 
         * @param function
         * @return
         */
        public <T> Operator<T, R> get(ResultSetMapper<? extends T> function) {
            return new QuerySelectOperator<T, R>(builder, function, operatorType);
        }

        /**
         * See {@link Builder#autoMap(Class)}.
         * 
         * @param cls
         * @return
         */
        public <S> Operator<S, R> autoMap(Class<S> cls) {
            return get(Util.autoMap(cls));
        }

        /**
         * Automaps the first column of the ResultSet into the target class
         * <code>cls</code>.
         * 
         * @param cls
         * @return
         */
        public <S> Operator<S, R> getAs(Class<S> cls) {
            return get(Tuples.single(cls));
        }

        /**
         * Automaps all the columns of the {@link ResultSet} into the target
         * class <code>cls</code>. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls
         * @return
         */
        public <S> Operator<TupleN<S>, R> getTupleN(Class<S> cls) {
            return get(Tuples.tupleN(cls));
        }

        /**
         * Automaps all the columns of the {@link ResultSet} into {@link Object}
         * . See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls
         * @return
         */
        public <S> Operator<TupleN<Object>, R> getTupleN() {
            return get(Tuples.tupleN(Object.class));
        }

        /**
         * Automaps the columns of the {@link ResultSet} into the specified
         * classes. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls1
         * @param cls2
         * @return
         */
        public <T1, T2> Operator<Tuple2<T1, T2>, R> getAs(Class<T1> cls1, Class<T2> cls2) {
            return get(Tuples.tuple(cls1, cls2));
        }

        /**
         * Automaps the columns of the {@link ResultSet} into the specified
         * classes. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls1
         * @param cls2
         * @param cls3
         * @return
         */
        public <T1, T2, T3> Operator<Tuple3<T1, T2, T3>, R> getAs(Class<T1> cls1, Class<T2> cls2,
                Class<T3> cls3) {
            return get(Tuples.tuple(cls1, cls2, cls3));
        }

        /**
         * Automaps the columns of the {@link ResultSet} into the specified
         * classes. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls1
         * @param cls2
         * @param cls3
         * @param cls4
         * @return
         */
        public <T1, T2, T3, T4> Operator<Tuple4<T1, T2, T3, T4>, R> getAs(Class<T1> cls1,
                Class<T2> cls2, Class<T3> cls3, Class<T4> cls4) {
            return get(Tuples.tuple(cls1, cls2, cls3, cls4));
        }

        /**
         * Automaps the columns of the {@link ResultSet} into the specified
         * classes. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls1
         * @param cls2
         * @param cls3
         * @param cls4
         * @param cls5
         * @return
         */
        public <T1, T2, T3, T4, T5> Operator<Tuple5<T1, T2, T3, T4, T5>, R> getAs(Class<T1> cls1,
                Class<T2> cls2, Class<T3> cls3, Class<T4> cls4, Class<T5> cls5) {
            return get(Tuples.tuple(cls1, cls2, cls3, cls4, cls5));
        }

        /**
         * Automaps the columns of the {@link ResultSet} into the specified
         * classes. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls1
         * @param cls2
         * @param cls3
         * @param cls4
         * @param cls5
         * @param cls6
         * @return
         */
        public <T1, T2, T3, T4, T5, T6> Operator<Tuple6<T1, T2, T3, T4, T5, T6>, R> getAs(
                Class<T1> cls1, Class<T2> cls2, Class<T3> cls3, Class<T4> cls4, Class<T5> cls5,
                Class<T6> cls6) {
            return get(Tuples.tuple(cls1, cls2, cls3, cls4, cls5, cls6));
        }

        /**
         * Automaps the columns of the {@link ResultSet} into the specified
         * classes. See {@link #autoMap(Class) autoMap()}.
         * 
         * @param cls1
         * @param cls2
         * @param cls3
         * @param cls4
         * @param cls5
         * @param cls6
         * @param cls7
         * @return
         */
        public <T1, T2, T3, T4, T5, T6, T7> Operator<Tuple7<T1, T2, T3, T4, T5, T6, T7>, R> getAs(
                Class<T1> cls1, Class<T2> cls2, Class<T3> cls3, Class<T4> cls4, Class<T5> cls5,
                Class<T6> cls6, Class<T7> cls7) {
            return get(Tuples.tuple(cls1, cls2, cls3, cls4, cls5, cls6, cls7));
        }

    }

}
