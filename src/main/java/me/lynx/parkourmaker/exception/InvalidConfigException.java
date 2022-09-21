package me.lynx.parkourmaker.exception;

import me.lynx.parkourmaker.io.file.load.ConfigValues;

public class InvalidConfigException extends RuntimeException {

    public InvalidConfigException(ConfigValues value, Throwable cause) {
        super("Could not parse %value% value from config field %mapping%!"
                .replaceAll("%value%", value.getValue() == null ? "NULL" : value.getValue())
                .replaceAll("%mapping%", "'" + value + "'"), cause);
    }

}
