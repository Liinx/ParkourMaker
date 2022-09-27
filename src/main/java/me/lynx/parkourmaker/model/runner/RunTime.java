package me.lynx.parkourmaker.model.runner;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.exception.UnfinishedBreakException;
import me.lynx.parkourmaker.util.Utils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RunTime {

    private long starTime;
    private long stopTime;
    private final List<TimeBreak> breaks;
    private final String owningRunner;

    public RunTime(String owningRunner) {
        this.owningRunner = owningRunner;
        breaks = new ArrayList<>();
    }

    public RunTime(String owningRunner, long starTime, long stopTime, List<TimeBreak> breaks) {
        this.owningRunner = owningRunner;
        this.starTime = starTime;
        this.stopTime = stopTime;
        this.breaks = breaks;
    }

    public long getStarTime() {
        return starTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void start() {
        starTime = System.currentTimeMillis();
    }

    public boolean containsUnfinishedBreaks() {
        return breaks.stream().anyMatch(Predicate.not(TimeBreak::isComplete));
    }

    public void pause() {
        Supplier<Stream<TimeBreak>> supplier = () -> breaks.stream()
            .filter(Predicate.not(TimeBreak::isComplete));

        if (supplier.get().findAny().isEmpty()) breaks.add(new TimeBreak(this, System.currentTimeMillis()));
        else throw new UnfinishedBreakException(new Exception().getCause());
    }

    public void continueTime() {
        Supplier<Stream<TimeBreak>> supplier = () -> breaks.stream()
            .filter(Predicate.not(TimeBreak::isComplete));

        if (supplier.get().findAny().isEmpty()) throw new UnfinishedBreakException(new Exception().getCause());
        else supplier.get().findAny().get().continueTime();
    }

    public List<TimeBreak> getBreaks() {
        return breaks;
    }

    public void stop() {
        stopTime = System.currentTimeMillis();

        Runner runner = ParkourMakerPlugin.instance().getRunnerHandler().getRunnerFromPlayer(owningRunner);
        String savedTime = ParkourMakerPlugin.instance().getStorage().getBestTime(owningRunner, runner.getMap().getName());
        if (savedTime == null) {
            ParkourMakerPlugin.instance().getStorage().saveBestRunTime(owningRunner, runner.getMap().getName(),
                getTime(true));
            return;
        }

        Duration saved = Utils.savedTimeToDuration(savedTime);
        Duration current = Utils.savedTimeToDuration(getTime(true));
        if (current.toMillis() < saved.toMillis()) ParkourMakerPlugin.instance().getStorage()
            .saveBestRunTime(owningRunner, runner.getMap().getName(), getTime(true));
    }

    public void clear() {
        starTime = 0;
        stopTime = 0;
        breaks.clear();
        ParkourMakerPlugin.instance().getStorage().setRunTimestamps(owningRunner, null);
    }

    public String getTime(boolean containEmptyUnits) {
        long timeInMS = stopTime - starTime;
        long combinedBreak = 0;
        for (TimeBreak aBreak : breaks) {
            if (!aBreak.isComplete()) continue;
            combinedBreak += aBreak.getBreak();
        }
        timeInMS -= combinedBreak;
        return Utils.toReadableTime(timeInMS, containEmptyUnits);
    }

}