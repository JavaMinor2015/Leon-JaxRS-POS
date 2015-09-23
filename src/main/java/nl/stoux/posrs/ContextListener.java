package nl.stoux.posrs;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Leon Stam on 23-9-2015.
 */
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Huh?");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
