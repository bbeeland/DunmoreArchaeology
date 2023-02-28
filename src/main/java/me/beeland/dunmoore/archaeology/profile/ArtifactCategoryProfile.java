package me.beeland.dunmoore.archaeology.profile;

import com.google.common.collect.Lists;
import me.beeland.dunmoore.archaeology.Archaeology;
import me.beeland.dunmoore.archaeology.artifact.ArtifactRarity;
import net.andreinc.mockneat.unit.objects.Probabilities;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArtifactCategoryProfile {

    private Archaeology plugin;

    private int id;
    private String name;
    private double chance;
    private ItemStack item;
    private List<String> completionCommands;

    public ArtifactCategoryProfile(Archaeology plugin, int id, String name, double chance, ItemStack item) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.chance = chance;
        this.item = item;
        this.completionCommands = Lists.newArrayList();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getChance() {
        return chance;
    }

    public ItemStack getItem() {
        return item;
    }

    public List<String> getCompletionCommands() {
        return completionCommands;
    }

    public void addCompletionCommand(String command) {
        this.completionCommands.add(command);
    }

    public List<ArtifactProfile> getProfiles() {

        List<ArtifactProfile> artifactProfiles = Lists.newArrayList();

        for(ArtifactProfile profile : plugin.getArtifactProfileHandler().getArtifactProfiles()) {

            if(profile.getCategoryId() == id) {
                artifactProfiles.add(profile);
            }
        }
        return artifactProfiles;
    }

    public boolean isPlayerCompleted(Player player) {

        // All artifacts a player has discovered by ID
        Set<Integer> foundArtifacts = plugin.getArcheologistProfileHandler().getByUUID(player.getUniqueId()).getSavedArtifacts().keySet();
        // ALl discovered artifacts that are in this category
        List<Integer> foundCategory = foundArtifacts.stream().filter((i) -> i == this.id).collect(Collectors.toList());

        if(foundCategory.size() == getProfiles().size()) return true;

        return false;
    }

    public ArtifactProfile getRandomArtifactProfile() {

        Probabilities<ArtifactProfile> artifactProbabilities = plugin.getMockNeat().probabilites(ArtifactProfile.class);

        plugin.getArtifactProfileHandler().getArtifactProfiles().forEach(profile -> {

            plugin.getLogger().severe(profile.getName() + ";" + profile.getChance());
            artifactProbabilities.add(profile.getChance(), profile);
        });
        return artifactProbabilities.get();
    }

}
