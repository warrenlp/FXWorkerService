package sample.threads;

/**
 * Created by Warren on 7/23/2015.
 */
public class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {
    private String handlerId;

    public ThreadExceptionHandler(String handleId) { this.handlerId = handleId; }

    public ThreadExceptionHandler() {
        // Do nothing
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(this + " caught Exception in Thread - \"" + t.getName() + "\" => " + e);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + this.hashCode() +
                (handlerId == null || "".equals(handlerId) ? "" :
                "(\"" + handlerId + "\")");
    }
}
