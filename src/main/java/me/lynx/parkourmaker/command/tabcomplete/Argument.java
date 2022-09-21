package me.lynx.parkourmaker.command.tabcomplete;

import java.util.Set;

public class Argument {

    private final int position;
    private final Set<String> possibilities;

    public Argument(int position, Set<String> possibilities) {
        this.position = position;
        this.possibilities = possibilities;
    }

    public void addPossibility(String possibility) {
        possibilities.add(possibility);
    }

    public int getPosition() {
        return position;
    }

    public Set<String> getPossibilities() {
        return possibilities;
    }

}
