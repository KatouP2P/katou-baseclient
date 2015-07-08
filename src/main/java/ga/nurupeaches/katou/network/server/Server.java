package ga.nurupeaches.katou.network.server;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

public interface Server {

    public void tick();

    public void close() throws IOException;

    public ExecutorService getService();

    class NamedForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

        private final AtomicInteger THREAD_POOL_COUNT = new AtomicInteger(0);
        private final String THREAD_PREFIX;

        public NamedForkJoinWorkerThreadFactory(String prefix){
            THREAD_PREFIX = prefix;
        }

        public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            ForkJoinWorkerThread thread = new NamedForkJoinWorkerThread(pool);
            thread.setName(THREAD_PREFIX + THREAD_POOL_COUNT.incrementAndGet());
            return thread;
        }

    }

    class NamedForkJoinWorkerThread extends ForkJoinWorkerThread {

        public NamedForkJoinWorkerThread(ForkJoinPool pool){
            super(pool);
        }

    }

}
