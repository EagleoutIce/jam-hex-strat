package de.flojo.jam.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// TODO: polish this dirty fun :)
public class HexStratLogger {
    private static final Logger log = Logger.getLogger(HexStratLogger.class.getName());
    private static final HexStratLogger HEX_START_LOGGER = new HexStratLogger();
    private final Logger logger;

    private HexStratLogger() {
        LogManager.getLogManager().reset();
        logger = Logger.getLogger("");
        logger.setLevel(Level.INFO);
        try {
            final var consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new SimpleFormatter());
            final var fileHandler = new FileHandler("game.log", 50000, 1, true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);
            logger.addHandler(fileHandler);
        } catch (Exception exception) {
            log.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    public static Logger log() {
        return HEX_START_LOGGER.logger;
    }
}
