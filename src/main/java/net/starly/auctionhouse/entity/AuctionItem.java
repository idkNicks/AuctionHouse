package net.starly.auctionhouse.entity;

import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * AuctionItem 클래스는 경매에서 판매되는 아이템에 대한 정보를 저장합니다.
 *
 * @since 2023-05-23
 * @author idkNicks
 */
public record AuctionItem(UUID sellerId, ItemStack itemStack, long price, LocalDateTime expiryTime) {}