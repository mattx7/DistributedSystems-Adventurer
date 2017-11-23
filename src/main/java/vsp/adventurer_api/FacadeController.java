package vsp.adventurer_api;

import static spark.Spark.get;

public enum FacadeController {
    Singleton;

    public void run() {
        get("/hello", (req, res) -> "Hello World");
    }
}