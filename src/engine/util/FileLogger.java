package engine.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileLogger {
    Logger logger;
    FileHandler fh;

    static HashMap<String, FileLogger> cache = new HashMap<>();
    public static FileLogger get(String file) {
        if (!cache.containsKey(file)) {
            cache.put(file, new FileLogger(file));
        }

        return cache.get(file);
    }

    private FileLogger(String file) {
        logger = Logger.getLogger(file);

        try {
            fh = new FileHandler(file);
            logger.addHandler(fh);

            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void info(String msg) {
        logger.info(msg);
    }
    public void error(String msg) {
        logger.severe(msg);
    }
}
