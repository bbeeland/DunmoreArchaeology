package me.beeland.dunmoore.archaeology.commands;

import me.beeland.dunmoore.archaeology.Archaeology;
import me.beeland.dunmoore.archaeology.profile.ArcheologistProfile;
import me.beeland.dunmoore.archaeology.profile.ArtifactProfile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class ArtifactAdminCommand implements CommandExecutor, TabExecutor {

    private Archaeology plugin;

    public ArtifactAdminCommand(Archaeology plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginCommand("artifactadmin").setTabCompleter(this);
    }

    /**
     * /artifactadmin <player> <discoverall/removeall/discover>
     */

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(cmd.getName() != "artifactadmin") return true;
        if(!sender.hasPermission("dunmoorearchaeology.admin")) {
            sender.sendMessage(plugin.getMessage("Lang.No-Permission"));
            return true;
        }

        /**
         * Include discoverall/removeall
         */
        if(args.length == 2) {

            Player player = Bukkit.getPlayer(args[0]);
            ArcheologistProfile archeologistProfile = plugin.getArcheologistProfileHandler().getByUUID(player.getUniqueId());

            if(player == null) {
                sender.sendMessage(plugin.getMessage("Lang.Invalid-Target"));
                return true;
            }

            if(args[1].equalsIgnoreCase("discoverall")) {

                archeologistProfile.discoverAllArtifacts();
                sender.sendMessage(plugin.getMessage("Lang.Discovered-All"));
                return true;
            }

            if(args[1].equalsIgnoreCase("removeall")) {

                archeologistProfile.removeAllArtifacts();
                sender.sendMessage(plugin.getMessage("Lang.Removed-All"));
                return true;
            }

        }

        /**
         * will include specific artifact to discover (/artifactadmin <player> discover <name>
         */
        if(args.length == 3) {

            Player player = Bukkit.getPlayer(args[0]);
            ArcheologistProfile archeologistProfile = plugin.getArcheologistProfileHandler().getByUUID(player.getUniqueId());

            if(args[1].equalsIgnoreCase("discover")) {

                if(player == null) {
                    sender.sendMessage(plugin.getMessage("Lang.Invalid-Target"));
                    return true;
                }

                ArtifactProfile artifactProfile = plugin.getArtifactProfileHandler().getByName(args[2]);

                if(artifactProfile == null) {
                    sender.sendMessage(plugin.getMessage("Lang.Invalid-Artifact"));
                    return true;
                }

                archeologistProfile.addSavedArtifact(artifactProfile.getId());
                sender.sendMessage(plugin.getMessage("Lang.Added-Artifact"));
                return true;
            }


        }

        sender.sendMessage(plugin.getMessage("Lang.Unknown-Arguments"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
