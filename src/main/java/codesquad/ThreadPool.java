package codesquad;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(20);

    private ThreadPool() {
    }

    public static void submit(Runnable task) {
        executorService.submit(task);
    }
}
