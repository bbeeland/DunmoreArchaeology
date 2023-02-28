package me.beeland.dunmoore.archaeology.handler;

import com.google.common.collect.Lists;
import de.tr7zw.nbtapi.NBTItem;
import me.beeland.dunmoore.archaeology.Archaeology;
import me.beeland.dunmoore.archaeology.artifact.ArtifactObtainMethod;
import me.beeland.dunmoore.archaeology.artifact.ArtifactRarity;
import me.beeland.dunmoore.archaeology.profile.ArtifactCategoryProfile;
import me.beeland.dunmoore.archaeology.profile.ArtifactProfile;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ArtifactProfileHandler {

    private Archaeology plugin;
    private List<ArtifactProfile> artifactProfiles;

    public ArtifactProfileHandler(Archaeology plugin) {

        this.plugin = plugin;
        this.artifactProfiles = Lists.newArrayList();

    }

    public int getArtifactChance() {
        return plugin.getPluginConfig().getInteger("Options.Artifact-Chance");
    }

    public List<ArtifactProfile> getArtifactProfiles() {
        return artifactProfiles;
    }

    public boolean isArtifact(ItemStack itemStack) {
        return new NBTItem(itemStack).hasKey("artifactId");
    }

    public int getArtifactId(ItemStack item) {
        return new NBTItem(item).getInteger("artifactId");
    }

    public boolean registerArtifactProfile(ArtifactProfile profile) {
        return artifactProfiles.add(profile);
    }

    public boolean unregisterArtifactProfile(ArtifactProfile profile) {
        return artifactProfiles.remove(profile);
    }

    public ArtifactProfile getByItem(ItemStack item) {
        return getById(getArtifactId(item));
    }

    public ArtifactProfile getById(int id) {

        for(ArtifactProfile profile : artifactProfiles) {
            if(profile.getId() == id) return profile;
        }
        return null;
    }

    public ArtifactProfile getByName(String name) {

        for(ArtifactProfile profile : artifactProfiles) {
            if(profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        return null;
    }


    public void load(ArtifactCategoryProfile categoryProfile, ConfigurationSection section) {

        int id = section.getInt("ID");
        ArtifactObtainMethod obtainMethod = ArtifactObtainMethod.valueOf(section.getString("Obtain-Method"));
        List<String> obtainCommands = section.getStringList("Obtain-Actions");
        List<Biome> discoverableBiomes = plugin.getBiomeGroupByName(section.getString("Discoverable-Biomes"));
        List<Material> discoverableTypes = plugin.getMaterialGroupByName(section.getString("Discoverable-Types"));
        ArtifactRarity rarity = ArtifactRarity.valueOf(section.getString("Rarity"));
        double chance = section.getDouble("Chance");

        ItemStack item = plugin.deserializeItem(section.getConfigurationSection("Item"));
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger("artifactId", id);

        ArtifactProfile profile = new ArtifactProfile(plugin, id, categoryProfile.getId(), section.getName(), chance, nbtItem.getItem());

        profile.setObtainMethod(obtainMethod);
        profile.setExecuteCommands(obtainCommands);
        profile.setDiscoveryBiomes(discoverableBiomes);
        profile.setDiscoveryTypes(discoverableTypes);
        profile.setRarity(rarity);

        registerArtifactProfile(profile);
    }

}
