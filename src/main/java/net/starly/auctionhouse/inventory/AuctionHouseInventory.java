package net.starly.auctionhouse.inventory;

import lombok.AllArgsConstructor;
import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.entity.impl.WarehouseItem;
import net.starly.auctionhouse.manager.AuctionHouseListenerManager;
import net.starly.auctionhouse.manager.WarehouseListenerManager;
import net.starly.auctionhouse.page.AuctionHousePage;
import net.starly.auctionhouse.page.AuctionHousePageHolder;
import net.starly.auctionhouse.page.PaginationManager;
import net.starly.auctionhouse.page.WarehousePageHolder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

@AllArgsConstructor
public class AuctionHouseInventory {

    private final Inventory inventory;

    public void onClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        AuctionHousePageHolder paginationHolder = (AuctionHousePageHolder) inventory.getHolder();
        PaginationManager paginationManager = paginationHolder.getPaginationManager();

        if (event.getSlot() == paginationHolder.getPrevButtonSlot()) {
            if (!paginationManager.hasPrevPage()) return;
            paginationManager.prevPage();
            AuctionHouseListenerManager.pageInventory(player, paginationHolder);
            player.playSound(player.getLocation(), Sound.valueOf("ITEM_BOOK_PAGE_TURN"), 2, 1);
        }

        if (event.getSlot() == paginationHolder.getNextButtonSlot()) {
            if (!paginationManager.hasNextPage()) return;
            paginationManager.nextPage();
            AuctionHouseListenerManager.pageInventory(player, paginationHolder);
            player.playSound(player.getLocation(), Sound.valueOf("ITEM_BOOK_PAGE_TURN"), 2, 1);
        }

        if (event.getSlot() == paginationHolder.getWarehouseButtonSlot()) {
            WarehouseListenerManager.openWarehouse(player);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2, 1);
        }
    }
}
