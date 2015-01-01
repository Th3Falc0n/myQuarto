package myQuarto;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import myQuarto.server.QuartoServer;

public class ServerApplication {
    public static void main(String... args) {
        Logger.getGlobal().setFilter(new Filter() {
            @Override
            public boolean isLoggable(LogRecord record) {
                // TODO Auto-generated method stub
                return Options.ENABLE_LOGGING;
            }
        });
        
        new QuartoServer().run();
    }
}
