package me.lynx.parkourmaker.model.runner;

public class TimeBreak {

    private final long pauseTime;
    private long continueTime;
    private int id;
    private RunTime owningTime;

    protected TimeBreak(RunTime owningTime, long pauseTime) {
        this.pauseTime = pauseTime;
        this.owningTime = owningTime;
        id = generateID();
    }

    public TimeBreak(int id, long pauseTime, long continueTime) {
        this.pauseTime = pauseTime;
        this.continueTime = continueTime;
        this.id = id;
    }

    public long getBreak() {
        return continueTime - pauseTime;
    }

    public boolean isComplete() {
        return pauseTime != 0 && continueTime != 0;
    }

    public long getPauseTime() {
        return pauseTime;
    }

    public long getContinueTime() {
        return continueTime;
    }

    public void continueTime() {
        continueTime = System.currentTimeMillis();
    }

    public void setOwningTime(RunTime owningTime) {
        this.owningTime = owningTime;
    }

    public int getId() {
        return id;
    }

    private int generateID() {
        return owningTime.getBreaks().size() + 1;
    }

}