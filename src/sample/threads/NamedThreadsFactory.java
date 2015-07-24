package sample.threads;

import java.util.concurrent.ThreadFactory;

/**
 * Created by Warren on 7/23/2015.
 */
public class NamedThreadsFactory implements ThreadFactory {
    private static int count = 0;
    private static String NAME = "NamedThread-";

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, NAME + ++count);
        t.setUncaughtExceptionHandler(new ThreadExceptionHandler());
        return t;
    }
}
