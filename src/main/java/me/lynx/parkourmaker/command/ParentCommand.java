package me.lynx.parkourmaker.command;

import java.util.List;

public interface ParentCommand extends Command {

    ChildCommand getCommandByName(String commandName);

    List<ChildCommand> getAllCommands();

    void addChildCommand(ChildCommand childCommand);

}
