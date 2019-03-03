package org.amitynation.botstudio.screenplay.command;

import org.amitynation.botstudio.screenplay.Screenplay;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Example of how to create a simple command.
 * Register command by either running ScreenplayCommandProcessor.instance.registerCommand(new CommandBotExample());
 * OR add it in the internal list of ScreenplayCommandProcessor.
 */
public class CommandBotExample extends ScreenplayCommand {

    private CommandBotExample() {
        // Capitalization doesn't matter here since the command processor uses toLowerCase() on both of these.
        super("GiveItem", new String[]{"Give"});
    }

    /**
     * Simple command to show how to add a command.
     */
    @Override
    public void onCommand(Screenplay screenplay, String command, String[] args) {
        // DiscordCommand arg can either be "giveitem" or "give" (commandName or alias)
        if (args.length != 2) {
            screenplay.logError(missingArgs("<Player> <Item Type>"));
            return;
        }
        List<Player> players = Bukkit.getServer().matchPlayer(args[0]);
        if (players.isEmpty()) {
            screenplay.logError("Cannot find player with name " + args[0]);
            return;
        }
        Material material = Material.matchMaterial(args[1]);
        if (material == null) {
            screenplay.logError("Cannot find item with name " + args[1]);
            return;
        }

        ItemStack itemStack = new ItemStack(material, 64);
        players.get(0).getInventory().addItem(itemStack);
    }
}
