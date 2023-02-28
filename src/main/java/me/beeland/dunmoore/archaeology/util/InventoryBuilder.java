package me.beeland.dunmoore.archaeology.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryBuilder {

    private Inventory inventory;

    public InventoryBuilder(int size) {
        this.inventory = Bukkit.createInventory(null, size);
    }

    public InventoryBuilder(int size, String title) {
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    public InventoryBuilder setItem(ItemStack item, int... slots) {

        for(int i : slots) {
            inventory.setItem(i, item);
        }
        return this;
    }

    public Inventory build() {
        return inventory;
    }

}
