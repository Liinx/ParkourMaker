package me.lynx.parkourmaker.exception;

public class UnfinishedBreakException extends RuntimeException {

    public UnfinishedBreakException(Throwable cause) {
        super("There is a unfinished break, new one cannot bet started before previous one is finished!", cause);
    }

}