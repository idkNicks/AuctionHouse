package net.starly.auctionhouse.manager;

import net.starly.auctionhouse.entity.impl.WarehouseItem;
import net.starly.auctionhouse.page.PaginationHolder;
import net.starly.auctionhouse.page.PaginationManager;
import net.starly.auctionhouse.page.WarehousePageHolder;
import net.starly.auctionhouse.storage.PlayerItemStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class WarehouseInventoryManager extends InventoryListenerManager {

    private static WarehouseInventoryManager instance;

    private WarehouseInventoryManager() {}

    public static WarehouseInventoryManager getInstance() {
        if (instance == null) instance = new WarehouseInventoryManager();
        return instance;
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        player.sendMessage("클릭했네");
        event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        AuctionHouseInventoryManager auctionHouseInventoryManager = AuctionHouseInventoryManager.getInstance();
        auctionHouseInventoryManager.openInventory(player);
    }

    @Override
    public void openInventory(Player player) {
        List<ItemStack> items = PlayerItemStorage.loadExpiredItem(player.getUniqueId());

        PaginationManager<WarehouseItem> warehousePaginationManager = new PaginationManager(items);
        WarehousePageHolder warehouseHolder = new WarehousePageHolder(player.getUniqueId(), warehousePaginationManager, 50, 48);

        openInventoryAndRegisterEvent(player, warehouseHolder.getInventory());
    }

    @Override
    public void pageInventory(Player player, PaginationHolder paginationHolder) {
        openInventoryAndRegisterEvent(player, paginationHolder.getInventory());
    }
}
