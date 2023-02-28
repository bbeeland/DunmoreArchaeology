package me.beeland.dunmoore.archaeology.util.meta;

import com.google.common.collect.Lists;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemMetaBuilder {

    private ItemStack item;
    private ItemMeta meta;

    public ItemMetaBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemMetaBuilder displayName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemMetaBuilder addLore(String... lines) {
        List<String> lore = meta.getLore() == null ? Lists.newArrayList() : meta.getLore();
        Arrays.stream(lines).forEach(line -> lore.add(line));
        return this;
    }

    public ItemMetaBuilder addLore(List<String> lines) {

        List<String> lore = meta.getLore() == null ? Lists.newArrayList() : meta.getLore();

        for(String line : lines) {
            lore.add(line);
        }
        meta.setLore(lore);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

}
