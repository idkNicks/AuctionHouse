package net.starly.auctionhouse.auctionhouse.entity;

import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionItem(UUID sellerId, ItemStack itemStack, long price, LocalDateTime expiryTime) {}