package uk.ac.manchester.cs.owl.owlapi.concurrent;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import javax.annotation.Nullable;

/**
 * Matthew Horridge Stanford Center for Biomedical Informatics Research 13/04/15
 */
class NoOpLock implements Lock, Serializable {

    public static final NoOpCondition NO_OP_CONDITION = new NoOpCondition();

    @Override
    public void lock() {}

    @Override
    public void lockInterruptibly() {}

    @Override
    public boolean tryLock() {
        return true;
    }

    @Override
    public boolean tryLock(long time, @Nullable TimeUnit unit) {
        return true;
    }

    @Override
    public void unlock() {}

    @Override
    public Condition newCondition() {
        return NO_OP_CONDITION;
    }

    private static class NoOpCondition implements Condition, Serializable {

        public NoOpCondition() {}

        @Override
        public void await() {}

        @Override
        public void awaitUninterruptibly() {}

        @Override
        public long awaitNanos(long nanosTimeout) {
            return 0;
        }

        @Override
        public boolean await(long time, @Nullable TimeUnit unit) {
            return true;
        }

        @Override
        public boolean awaitUntil(@Nullable Date deadline) {
            return true;
        }

        @Override
        public void signal() {}

        @Override
        public void signalAll() {}
    }
}
