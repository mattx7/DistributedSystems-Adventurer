package vsp;

import org.apache.log4j.Logger;
import vsp.adventurer_api.FacadeController;

import java.io.IOException;

/**
 * Runs application and interactions.
 */
public class Application {
    private static Logger LOG = Logger.getLogger(Application.class);

    /**
     * Holds only the main method an instance is not necessary.
     */
    private Application() {
    }

    public static void main(String[] args) throws IOException {
        LOG.debug("Starting application...");

        FacadeController.Singleton.run();
    }
}