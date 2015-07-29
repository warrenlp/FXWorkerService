package sample;

/**
 * Created by warre_000 on 7/22/2015.
 */
public class ThrowerClass implements Runnable {
    @Override
    public void run() {
        throw new NullPointerException("I threw this.");
    }
}
