package net.starly.auctionhouse.entity;

import org.bukkit.inventory.ItemStack;

public record WarehouseItem(ItemStack itemStack) implements AuctionItemOrStack {

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }
}
