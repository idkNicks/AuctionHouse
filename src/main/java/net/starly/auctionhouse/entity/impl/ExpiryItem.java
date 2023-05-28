package net.starly.auctionhouse.entity.impl;

import net.starly.auctionhouse.entity.AuctionItemOrStack;
import org.bukkit.inventory.ItemStack;

public record ExpiryItem(ItemStack itemStack) implements AuctionItemOrStack {

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }
}
