package net.starly.auctionhouse.page;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.builder.ItemBuilder;
import net.starly.auctionhouse.entity.AuctionItem;
import net.starly.auctionhouse.entity.WarehouseItem;
import net.starly.auctionhouse.storage.PlayerItemStorage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record WarehouseHolder(UUID uuid, PaginationManager paginationManager, int nextButtonSlot, int prevButtonSlot) implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = AuctionHouse.getInstance().getServer().createInventory(this, 54, "만료아이템 [" + paginationManager.getCurrentPage() + "]");

        List<ItemStack> expiredItems = PlayerItemStorage.loadExpiredItem(uuid);
        List<WarehouseItem> warehouseItems = new ArrayList<>();

        for (ItemStack itemStack : expiredItems) {
            WarehouseItem warehouseItem = new WarehouseItem(itemStack);
            warehouseItems.add(warehouseItem);
        }

        AuctionHousePage<WarehouseItem> page = new AuctionHousePage<>(1, warehouseItems);

        for (int i = 0; i < page.itemStacks().size(); i++) {
            inventory.setItem(i, page.itemStacks().get(i).itemStack());
        }

        ItemStack nextPageItem = new ItemBuilder(Material.ARROW)
                .setName("다음 페이지")
                .build();

        ItemStack prevPageItem = new ItemBuilder(Material.ARROW)
                .setName("이전 페이지")
                .build();

        inventory.setItem(nextButtonSlot, nextPageItem);
        inventory.setItem(prevButtonSlot, prevPageItem);

        return inventory;
    }
}