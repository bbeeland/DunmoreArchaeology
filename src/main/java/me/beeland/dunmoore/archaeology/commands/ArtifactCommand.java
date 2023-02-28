package me.beeland.dunmoore.archaeology.commands;

import me.beeland.dunmoore.archaeology.Archaeology;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ArtifactCommand implements CommandExecutor {

    private Archaeology plugin;

    public ArtifactCommand(Archaeology plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("Invalid-Sender"));
            return true;
        }

        if(!sender.hasPermission("archeology.command.artifacts")) {
            sender.sendMessage(plugin.getMessage("No-Permission"));
            return true;
        }

        Player player = (Player) sender;
        player.openInventory(plugin.getArtifactBookMenu(player));

        return true;
    }
}
