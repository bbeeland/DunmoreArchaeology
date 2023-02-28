package me.beeland.dunmoore.archaeology.profile;

import me.beeland.dunmoore.archaeology.Archaeology;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class ArcheologistProfile {

    private Archaeology plugin;
    private UUID owner;

    private Map<Integer, Integer> savedArtifacts;

    public ArcheologistProfile(Archaeology plugin, UUID owner, Map<Integer, Integer> savedArtifacts) {
        this.plugin = plugin;
        this.owner = owner;
        this.savedArtifacts = savedArtifacts;
    }

    public UUID getOwner() {
        return owner;
    }

    public Map<Integer, Integer> getSavedArtifacts() {
        return savedArtifacts;
    }

    public boolean hasArtifact(int id) {
        return savedArtifacts.containsKey(id);
    }

    public void addSavedArtifact(ItemStack item) {
        this.addSavedArtifact(plugin.getArtifactProfileHandler().getArtifactId(item));
    }

    public void addSavedArtifact(ArtifactProfile artifactProfile) {
        this.addSavedArtifact(artifactProfile.getId(), 1);
    }

    public void addSavedArtifact(ItemStack item, int amount) {
        this.addSavedArtifact(plugin.getArtifactProfileHandler().getArtifactId(item), amount);
    }

    public void addSavedArtifact(int id) {
       this.addSavedArtifact(id, 1);
    }

    public void addSavedArtifact(int id, int amount) {

        if(savedArtifacts.containsKey(id)) {
            savedArtifacts.put(id, savedArtifacts.getOrDefault(id, 0) + amount);
            return;
        }

        savedArtifacts.put(id, amount);
    }

    public void removeSavedArtifacts(int id) {
        this.savedArtifacts.remove(id);
    }

    public void discoverAllArtifacts() {

        for(ArtifactProfile artifactProfile : plugin.getArtifactProfileHandler().getArtifactProfiles()) {

            if(savedArtifacts.containsKey(artifactProfile.getId())) return;
            savedArtifacts.put(artifactProfile.getId(), 1);
        }

    }

    public void removeAllArtifacts() {
        savedArtifacts.clear();
    }

}
