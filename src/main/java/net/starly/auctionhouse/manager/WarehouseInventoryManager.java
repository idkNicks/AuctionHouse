package net.starly.auctionhouse.manager;

import net.starly.auctionhouse.entity.impl.WarehouseItem;
import net.starly.auctionhouse.page.PaginationManager;
import net.starly.auctionhouse.page.WarehousePageHolder;
import net.starly.auctionhouse.storage.PlayerItemStorage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WarehouseInventoryManager extends InventoryListenerBase {

    private static WarehouseInventoryManager instance;

    private WarehouseInventoryManager() {}

    public static WarehouseInventoryManager getInstance() {
        if (instance == null) instance = new WarehouseInventoryManager();
        return instance;
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        event.setCancelled(true);

        WarehousePageHolder warehousePageHolder = (WarehousePageHolder) inventory.getHolder();
        PaginationManager<WarehouseItem> paginationManager = warehousePageHolder.getPaginationManager();

        if (event.getSlot() == warehousePageHolder.getPrevButtonSlot()) {
            if (!paginationManager.hasPrevPage()) return;
            player.sendMessage("이전페이지");
            paginationManager.prevPage();
            pageInventory(player, warehousePageHolder);
            player.playSound(player.getLocation(), Sound.valueOf("ITEM_BOOK_PAGE_TURN"), 2, 1);
        }

        else if (event.getSlot() == warehousePageHolder.getNextButtonSlot()) {
            if (!paginationManager.hasNextPage()) return;
            player.sendMessage("다음페이지");
            paginationManager.nextPage();
            pageInventory(player, warehousePageHolder);
            player.playSound(player.getLocation(), Sound.valueOf("ITEM_BOOK_PAGE_TURN"), 2, 1);
        }

        else {
            int clickedSlot = event.getSlot();
            List<WarehouseItem> items = new ArrayList<>(paginationManager.getCurrentPageData().getItemStacks());
            if (clickedSlot >= 0 && clickedSlot < items.size()) {
                WarehouseItem clickedItem = items.get(clickedSlot);
                player.getInventory().addItem(clickedItem.itemStack());
                PlayerItemStorage.removeItem(player.getUniqueId(), clickedItem);

                Iterator<WarehouseItem> iterator = items.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().equals(clickedItem)) {
                        iterator.remove();
                        break;
                    }
                }

                paginationManager.getCurrentPageData().setItemStacks(items);

                if (items.isEmpty() && paginationManager.hasPrevPage()) {
                    player.sendMessage("이전페이지");
                    paginationManager.prevPage();
                }
                pageInventory(player, warehousePageHolder);
            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
//        Player player = (Player) event.getPlayer();
//        AuctionHouseInventoryManager auctionHouseInventoryManager = AuctionHouseInventoryManager.getInstance();
//        AuctionHouse.getInstance().getServer().getScheduler().runTaskLater(AuctionHouse.getInstance(), () ->
//                auctionHouseInventoryManager.openInventory(player), 1);
    }

    @Override
    public void openInventory(Player player) {
        List<WarehouseItem> items = PlayerItemStorage.loadExpiredItem(player.getUniqueId());

        PaginationManager<WarehouseItem> paginationManager = new PaginationManager<>(items);
        WarehousePageHolder<WarehouseItem> paginationInventoryHolder = new WarehousePageHolder<>(player.getUniqueId(), paginationManager, 50, 48);

        openInventoryAndRegisterEvent(player, paginationInventoryHolder.getInventory());
    }
}
