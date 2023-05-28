package net.starly.auctionhouse.util;

import net.starly.auctionhouse.builder.ItemBuilder;
import net.starly.auctionhouse.context.MessageContent;
import net.starly.auctionhouse.context.MessageType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PaginationItemUtil {

    private PaginationItemUtil() {}
    
    private static MessageContent content = MessageContent.getInstance();
    
    public static @NotNull ItemStack createNextPageItem() {
        return new ItemBuilder(Material.valueOf(content.getMessage(MessageType.AUCTIONHOUSE, "items.nextPage.material").orElse("ARROW")))
                .setName(content.getMessage(MessageType.AUCTIONHOUSE, "items.nextPage.displayname").orElse(""))
                .setLore(content.getMessages(MessageType.AUCTIONHOUSE, "items.nextPage.lore"))
                .build();
    }

    public static @NotNull ItemStack createPrevPageItem() {
        return new ItemBuilder(Material.valueOf(content.getMessage(MessageType.AUCTIONHOUSE, "items.prevPage.material").orElse("ARROW")))
                .setName(content.getMessage(MessageType.AUCTIONHOUSE, "items.prevPage.displayname").orElse(""))
                .setLore(content.getMessages(MessageType.AUCTIONHOUSE, "items.prevPage.lore"))
                .build();
    }

    public static @NotNull ItemStack createExpiryItem() {
        return new ItemBuilder(Material.valueOf(content.getMessage(MessageType.AUCTIONHOUSE, "items.expiry.material").orElse("CHEST")))
                .setName(content.getMessage(MessageType.AUCTIONHOUSE, "items.expiry.displayname").orElse(""))
                .setLore(content.getMessages(MessageType.AUCTIONHOUSE, "items.expiry.lore"))
                .build();
    }

    public static @NotNull ItemStack createWarehouseItem() {
        return new ItemBuilder(Material.valueOf(content.getMessage(MessageType.AUCTIONHOUSE, "items.warehouse.material").orElse("CHEST")))
                .setName(content.getMessage(MessageType.AUCTIONHOUSE, "items.warehouse.displayname").orElse(""))
                .setLore(content.getMessages(MessageType.AUCTIONHOUSE, "items.warehouse.lore"))
                .build();
    }
}
