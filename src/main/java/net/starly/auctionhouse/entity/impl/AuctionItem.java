package net.starly.auctionhouse.entity.impl;

import net.starly.auctionhouse.entity.AuctionItemOrStack;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionItem(UUID sellerId, ItemStack itemStack, long price, LocalDateTime expiryTime) implements AuctionItemOrStack {

    @Override
    public ItemStack getItemStack() {
        return itemStack.clone();
    }
}
