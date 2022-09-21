package me.lynx.parkourmaker.command;

import java.util.Set;

public interface Command {

    String getName();

    Set<String> getAliases();

}
