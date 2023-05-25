package net.starly.auctionhouse.manager;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.entity.impl.WarehouseItem;
import net.starly.auctionhouse.page.PaginationHolder;
import net.starly.auctionhouse.page.PaginationManager;
import net.starly.auctionhouse.page.WarehousePageHolder;
import net.starly.auctionhouse.storage.PlayerItemStorage;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;


public class WarehouseListenerManager extends InventoryListenerManager {

    private static WarehouseListenerManager instance;
    private Listener listener;

    private WarehouseListenerManager() {}

    public static WarehouseListenerManager getInstance() {
        if (instance == null) instance = new WarehouseListenerManager();
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

        player.sendMessage("닫았네");
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
