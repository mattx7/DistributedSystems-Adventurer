package vsp;

import vsp.adventurer_api.FacadeController;

import java.io.IOException;

/**
 * Runs application and interactions.
 */
public class Application {
    /**
     * Holds only the main method an instance is not necessary.
     */
    private Application() {
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Starting application...");

        FacadeController.Singleton.run();
    }
}