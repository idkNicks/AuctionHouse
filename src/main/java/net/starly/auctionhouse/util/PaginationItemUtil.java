package net.starly.auctionhouse.util;

import net.starly.auctionhouse.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PaginationItemUtil {

    public static @NotNull ItemStack createNextPageItem() {
        return new ItemBuilder(Material.ARROW)
                .setName("다음 페이지")
                .build();
    }

    public static @NotNull ItemStack createPrevPageItem() {
        return new ItemBuilder(Material.ARROW)
                .setName("이전 페이지")
                .build();
    }

    public static @NotNull ItemStack createWarehouseItem() {
        return new ItemBuilder(Material.CHEST)
                .setName("만료된 아이템")
                .build();
    }
}
