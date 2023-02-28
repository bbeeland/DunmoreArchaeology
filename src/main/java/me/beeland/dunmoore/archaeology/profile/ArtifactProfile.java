package me.beeland.dunmoore.archaeology.profile;

import com.google.common.collect.Lists;
import me.beeland.dunmoore.archaeology.Archaeology;
import me.beeland.dunmoore.archaeology.artifact.ArtifactObtainMethod;
import me.beeland.dunmoore.archaeology.artifact.ArtifactRarity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ArtifactProfile {

    private Archaeology plugin;

    private int id;
    private int categoryId;
    private String name;
    private double chance;
    private ArtifactRarity rarity;
    private ArtifactObtainMethod obtainMethod;
    private List<Biome> discoveryBiomes;
    private List<Material> discoveryTypes;
    List<String> executeCommands;
    private ItemStack artifact;

    public ArtifactProfile(Archaeology plugin, int id, int categoryId, String name, double chance, ItemStack artifact) {
        this.plugin = plugin;
        this.categoryId = categoryId;
        this.id = id;
        this.name = name;
        this.chance = chance;
        this.artifact = artifact;

        this.rarity = ArtifactRarity.UNCOMMON;
        this.obtainMethod = ArtifactObtainMethod.BOTH;
        this.discoveryBiomes = Lists.newArrayList();
        this.discoveryTypes = Lists.newArrayList();
        this.executeCommands = Lists.newArrayList();
    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public ArtifactCategoryProfile getArtifactCategory() {
        return plugin.getArtifactCategoryProfileHandler().getByID(categoryId);
    }

    public ArtifactRarity getRarity() {
        return rarity;
    }

    public void setRarity(ArtifactRarity rarity) {
        this.rarity = rarity;
    }

    public String getName() {
        return name;
    }

    public double getChance() {
        return chance;
    }

    public ArtifactObtainMethod getObtainMethod() {
        return obtainMethod;
    }

    public List<String> getExecuteCommands() {
        return executeCommands;
    }

    public void setExecuteCommands(List<String> executeCommands) {
        this.executeCommands = executeCommands;
    }

    public void setObtainMethod(ArtifactObtainMethod obtainMethod) {
        this.obtainMethod = obtainMethod;
    }

    public List<Biome> getDiscoveryBiomes() {
        return discoveryBiomes;
    }

    public void setDiscoveryBiomes(List<Biome> discoveryBiomes) {
        this.discoveryBiomes = discoveryBiomes;
    }

    public boolean addDiscoveryBiome(Biome biome) {
        return discoveryBiomes.add(biome);
    }

    public boolean removeDiscoveryBiome(Biome biome) {
        return discoveryBiomes.remove(biome);
    }

    public List<Material> getDiscoveryTypes() {
        return discoveryTypes;
    }

    public boolean addDiscoveryType(Material material) {
        return discoveryTypes.add(material);
    }

    public boolean removeDiscoveryType(Material material) {
        return discoveryTypes.remove(material);
    }

    public void setDiscoveryTypes(List<Material> discoveryTypes) {
        this.discoveryTypes = discoveryTypes;
    }

    public boolean isDiscoverable(Biome biome, Material material) {

        boolean biomeDiscoverable = discoveryBiomes.isEmpty() ? true : discoveryBiomes.contains(biome);
        boolean typeDiscoverable = discoveryTypes.isEmpty() ? true : discoveryTypes.contains(material);

        return (biomeDiscoverable && typeDiscoverable);
    }

    public ItemStack getArtifact(Player player) {

        ItemStack stack = artifact;
        ItemMeta meta = stack.getItemMeta();

        if(meta.hasDisplayName()) {
            meta.setDisplayName(plugin.getMessageWithPlaceholders(player, plugin.getConfig().getString(rarity.getColorPath()) + meta.getDisplayName()));
        }

        if(meta.hasLore()) {

            List<String> lore = Lists.newArrayList();

            for(String string : meta.getLore()) {
                lore.add(plugin.getMessageWithPlaceholders(player, string));
            }
            meta.setLore(lore);
        }

        stack.setItemMeta(meta);
        return stack;
    }

    public void execute(Player player) {

        ConsoleCommandSender sender = Bukkit.getConsoleSender();

        executeCommands.forEach(command -> {
            Bukkit.getServer().dispatchCommand(sender, plugin.getMessageWithPlaceholders(player, command));
        });
    }
}
