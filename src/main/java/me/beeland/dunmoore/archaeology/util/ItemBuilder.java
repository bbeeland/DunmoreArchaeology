package me.beeland.dunmoore.archaeology.util;

import me.beeland.dunmoore.archaeology.util.meta.ItemMetaBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder {

    private ItemStack item;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemMetaBuilder metaBuilder() {
        return new ItemMetaBuilder(item);
    }

}
