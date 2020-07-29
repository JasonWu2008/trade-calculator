package com.demo.calculator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;

public abstract class IOUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

    private IOUtils() {
    }

    public static String readFile(String file) {
        return readFromStream(getRootResourceAsStream(file));
    }

    private static InputStream getRootResourceAsStream(String file) {
        InputStream resourceAsStream = IOUtils.class.getClassLoader().getResourceAsStream(file);
        if (resourceAsStream == null) {
            throw new IllegalArgumentException(String.format("File not found [%s]", file));
        }
        return resourceAsStream;
    }

    private static String readFromStream(InputStream inputStream) {
        try {
            BufferedInputStream stream = new BufferedInputStream(inputStream);
            StringBuilder builder = new StringBuilder();
            byte[] collection = new byte[1024];
            int read;
            while ((read = stream.read(collection)) != -1) {
                builder.append(new String(collection, 0, read));
            }
            return builder.toString();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return "";
    }
}
