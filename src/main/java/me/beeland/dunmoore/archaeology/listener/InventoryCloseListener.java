package me.beeland.dunmoore.archaeology.listener;

import me.beeland.dunmoore.archaeology.Archaeology;
import me.beeland.dunmoore.archaeology.profile.ArtifactProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryCloseListener implements Listener {

    private Archaeology plugin;

    public InventoryCloseListener(Archaeology plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void onInventoryClose(InventoryCloseEvent e) {

        Inventory inventory = e.getInventory();
        Player player = (Player) e.getPlayer();

        if(!e.getView().getTitle().equalsIgnoreCase(plugin.getMessage("Lang.Artifact-Deposit-Title"))) return;
        if(e.getInventory().getItem(22) == null || e.getInventory().getItem(22).getType() == Material.AIR) return;

        ItemStack item = e.getInventory().getItem(22);
        ArtifactProfile artifactProfile = plugin.getArtifactProfileHandler().getByItem(item);

        if(artifactProfile == null) {
            player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item);
            return;
        }

        plugin.getArcheologistProfileHandler().getByUUID(player.getUniqueId()).addSavedArtifact(item);
        player.sendMessage("DEPOSITED ARTIFACT: " + artifactProfile.getName());
    }

}
