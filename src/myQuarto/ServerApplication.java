package myQuarto;

import java.util.logging.Level;
import java.util.logging.Logger;

import myQuarto.server.QuartoServer;

public class ServerApplication {
    public static void main(String... args) {
        Logger.getGlobal().setLevel(Level.FINEST);
        
        new QuartoServer().run();
    }
}
