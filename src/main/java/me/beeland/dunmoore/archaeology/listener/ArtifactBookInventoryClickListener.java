package me.beeland.dunmoore.archaeology.listener;

import me.beeland.dunmoore.archaeology.Archaeology;
import me.beeland.dunmoore.archaeology.profile.ArtifactCategoryProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ArtifactBookInventoryClickListener implements Listener {

    private Archaeology plugin;

    public ArtifactBookInventoryClickListener(Archaeology plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void onInventoryClick(InventoryClickEvent e) {

        String viewTitle = e.getView().getTitle();
        String artifactBookTitle = plugin.getMessage("Artifact-Book-Title");
        String artifactCategoryTitle = plugin.getMessage("Artifact-Category-Title");

        if(!(e.getWhoClicked() instanceof Player)) return;
        if(!(viewTitle.equals(artifactBookTitle)) && !(viewTitle.equals(artifactCategoryTitle))) return;
        if(e.getCurrentItem() == null) return;


        if(e.getSlot() == 53) {

            if(viewTitle.equals(artifactBookTitle)) {
                e.getWhoClicked().closeInventory();
            }

            if(viewTitle.equals(artifactCategoryTitle)) {
                e.getWhoClicked().openInventory(plugin.getArtifactBookMenu((Player) e.getWhoClicked()));
            }

        }

        ItemStack clicked = e.getCurrentItem();

        if(clicked != null && clicked.getType() != Material.AIR) {

            ArtifactCategoryProfile categoryProfile = plugin.getArtifactCategoryProfileHandler().getByItem(clicked);

            if(categoryProfile == null) {
                e.setCancelled(true);
                return;
            }

            e.getWhoClicked().openInventory(plugin.getArtifactMenu((Player) e.getWhoClicked(), categoryProfile));
        }
        e.setCancelled(true);
    }

}
