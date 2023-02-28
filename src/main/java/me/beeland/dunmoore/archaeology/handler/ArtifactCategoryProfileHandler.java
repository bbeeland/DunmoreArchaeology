package me.beeland.dunmoore.archaeology.handler;

import com.google.common.collect.Sets;
import de.tr7zw.nbtapi.NBTItem;
import me.beeland.dunmoore.archaeology.Archaeology;
import me.beeland.dunmoore.archaeology.PluginConfiguration;
import me.beeland.dunmoore.archaeology.artifact.ArtifactRarity;
import me.beeland.dunmoore.archaeology.profile.ArtifactCategoryProfile;
import me.beeland.dunmoore.archaeology.profile.ArtifactProfile;
import net.andreinc.mockneat.unit.objects.Probabilities;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Set;

public class ArtifactCategoryProfileHandler {

    private Archaeology plugin;
    private Set<ArtifactCategoryProfile> categoryProfiles;

    public ArtifactCategoryProfileHandler(Archaeology plugin) {
        this.plugin = plugin;
        this.categoryProfiles = Sets.newHashSet();
    }

    public Set<ArtifactCategoryProfile> getCategoryProfiles() {
        return categoryProfiles;
    }

    public boolean containsRarity(int categoryId, ArtifactRarity rarity) {

        for(ArtifactProfile artifactProfile : plugin.getArtifactProfileHandler().getArtifactProfiles()) {
            if(artifactProfile.getArtifactCategory().getId() == categoryId && artifactProfile.getRarity() == rarity) {
                return true;
            }
        }

        return false;
    }

    public boolean addCategoryProfile(ArtifactCategoryProfile categoryProfile) {
        return categoryProfiles.add(categoryProfile);
    }

    public boolean removeCategoryProfile(ArtifactCategoryProfile categoryProfile) {
        return categoryProfiles.remove(categoryProfile);
    }

    public boolean hasCategoryProfile(ArtifactCategoryProfile categoryProfile) {
        return categoryProfiles.contains(categoryProfile);
    }

    public ArtifactCategoryProfile getByID(int id) {

        for(ArtifactCategoryProfile categoryProfile : categoryProfiles) {
            if(categoryProfile.getId() == id) return categoryProfile;
        }

        return null;
    }

    public int getCategoryId(ItemStack item) {
        return new NBTItem(item).getInteger("category_id");
    }

    public ArtifactCategoryProfile getByItem(ItemStack item) {
        return getByID(getCategoryId(item));
    }

    public void load() {

        int registeredCount = 0;
        File artifactDirectory = plugin.getArtifactDirectory();

        for(File file : plugin.getArtifactDirectory().listFiles()) {

            PluginConfiguration artifactConfig = new PluginConfiguration(plugin, artifactDirectory, file.getName(), false);

            String name = artifactConfig.getString("Name");
            int id = artifactConfig.getInteger("ID");
            double chance = artifactConfig.getDouble("Chance");

            ItemStack item = plugin.deserializeItem(artifactConfig.getConfigSection("Item"));
            NBTItem nbtItem = new NBTItem(item);
            nbtItem.setInteger("category_id", id);

            ArtifactCategoryProfile categoryProfile = new ArtifactCategoryProfile(plugin, id, name, chance, nbtItem.getItem());
            addCategoryProfile(categoryProfile);
            registeredCount += 1;

            for(String artifactName : artifactConfig.getConfigSection("Artifacts").getKeys(false)) {
                plugin.getArtifactProfileHandler().load(categoryProfile, artifactConfig.getConfigSection("Artifacts." + artifactName));
            }
        }

        plugin.getLogger().info("Registered (" + registeredCount + ") artifact categories!");

    }

    public ArtifactCategoryProfile getRandomCategoryProfile() {

        Probabilities<ArtifactCategoryProfile> probabilityProfiles = plugin.getMockNeat().probabilites(ArtifactCategoryProfile.class);
        categoryProfiles.forEach(categoryProfile -> probabilityProfiles.add(categoryProfile.getChance(), categoryProfile));
        return probabilityProfiles.get();
    }
}
