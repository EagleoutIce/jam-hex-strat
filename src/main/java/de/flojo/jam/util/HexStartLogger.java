package de.flojo.jam.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// TODO: polish this dirty fun :)
public class HexStartLogger {
    private static final Logger log = Logger.getLogger(HexStartLogger.class.getName());
    private final Logger logger;
    private static final HexStartLogger HEX_START_LOGGER = new HexStartLogger();

    public static Logger log() {
        return HEX_START_LOGGER.logger;
    }

    private HexStartLogger() {
        LogManager.getLogManager().reset();
        logger = Logger.getLogger("");
        try {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new SimpleFormatter());
            FileHandler fileHandler = new FileHandler("game.log", 50000, 1, true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);
            logger.addHandler(fileHandler);
        } catch (Exception exception) {
            log.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }
}
