package me.beeland.dunmoore.archaeology.listener;

import me.beeland.dunmoore.archaeology.Archaeology;
import me.beeland.dunmoore.archaeology.artifact.ArtifactObtainMethod;
import me.beeland.dunmoore.archaeology.handler.ArcheologistProfileHandler;
import me.beeland.dunmoore.archaeology.profile.ArtifactCategoryProfile;
import me.beeland.dunmoore.archaeology.profile.ArtifactProfile;
import me.clip.placeholderapi.PlaceholderAPI;
import net.andreinc.mockneat.MockNeat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    private Archaeology plugin;
    private ArcheologistProfileHandler archeologistProfileHandler;
    private MockNeat mockNeat;
    private int artifactChance;

    public BlockBreakListener(Archaeology plugin) {
        this.plugin = plugin;
        this.mockNeat = plugin.getMockNeat();
        this.artifactChance = plugin.getArtifactProfileHandler().getArtifactChance();
        this.archeologistProfileHandler = plugin.getArcheologistProfileHandler();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) return;
        if(e.getBlock().hasMetadata("artifact_id ")) return;

        ArtifactObtainMethod activeMethod = getObtainMethodFromItem(e.getPlayer().getInventory().getItemInMainHand());
        Location location = e.getBlock().getLocation();

        if(e.getPlayer().getGameMode() == GameMode.CREATIVE && plugin.isCreativeDisabled()) return;
        if(!(mockNeat.bools().probability(artifactChance).get())) return;
        if(activeMethod == null) return;

        ArtifactCategoryProfile categoryProfile = plugin.getArtifactCategoryProfileHandler().getRandomCategoryProfile();
        ArtifactProfile artifactProfile = categoryProfile.getRandomArtifactProfile();

        if(artifactProfile == null) return;

        if(!artifactProfile.isDiscoverable(e.getBlock().getBiome(), e.getBlock().getType())) return;
        if((artifactProfile.getObtainMethod() == activeMethod) || (activeMethod == ArtifactObtainMethod.BOTH)) return;

        location.getWorld().dropItemNaturally(e.getBlock().getLocation(), artifactProfile.getArtifact(e.getPlayer()));

        boolean isBeforeCompleted = categoryProfile.isPlayerCompleted(e.getPlayer());

        plugin.getArcheologistProfileHandler().getByUUID(e.getPlayer().getUniqueId()).addSavedArtifact(artifactProfile);
        artifactProfile.execute(e.getPlayer());

        boolean isAfterCompleted = categoryProfile.isPlayerCompleted(e.getPlayer());

        if(!(isBeforeCompleted) && (isAfterCompleted)) {
            for(String completionCommand : categoryProfile.getCompletionCommands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(e.getPlayer(), completionCommand));
            }
        }

    }

    private ArtifactObtainMethod getObtainMethodFromItem(ItemStack item) {

        if(item == null) return ArtifactObtainMethod.BOTH;

        Material material = item.getType();
        String materialName = material.toString().toUpperCase();

        if(materialName.endsWith("_PICKAXE")) return ArtifactObtainMethod.MINE;
        if(materialName.endsWith("_SHOVEL")) return ArtifactObtainMethod.DIG;

        return ArtifactObtainMethod.BOTH;
    }

}
