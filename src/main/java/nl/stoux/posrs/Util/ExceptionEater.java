package nl.stoux.posrs.Util;

/**
 * Created by Leon Stam on 30-9-2015.
 */
public class ExceptionEater {

    /**
     * Run a piece of code while eating all exceptions
     * @param runnable The runnable
     */
    public static void eat(ExceptionRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            //Yummy
        }
    }

    @FunctionalInterface
    public interface ExceptionRunnable {
        void run() throws Exception;
    }

}
