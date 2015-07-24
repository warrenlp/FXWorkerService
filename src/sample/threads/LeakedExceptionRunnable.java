package sample.threads;

/**
 * Created by Warren on 7/23/2015.
 */
public class LeakedExceptionRunnable implements Runnable {
    private static int throwException = 0;

    @Override
    public void run() {
        System.out.println("I ran.");
        if (throwException > 1) {
            throw new RuntimeException("Leaked exception");
        } else {
            throwException++;
        }
    }
}
