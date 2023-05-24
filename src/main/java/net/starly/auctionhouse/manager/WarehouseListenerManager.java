package net.starly.auctionhouse.manager;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.entity.impl.WarehouseItem;
import net.starly.auctionhouse.inventory.WarehouseInventory;
import net.starly.auctionhouse.page.AuctionHousePageHolder;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WarehouseListenerManager {

    private static final Map<UUID, Listener> listenerMap = new HashMap<>();

    public static void openWarehouse(Player player) {
        List<ItemStack> items = PlayerItemStorage.loadExpiredItem(player.getUniqueId());

        PaginationManager<WarehouseItem> warehousePaginationManager = new PaginationManager(items);
        WarehousePageHolder warehouseHolder = new WarehousePageHolder(player.getUniqueId(), warehousePaginationManager, 50, 48);

        openInventoryAndRegisterEvent(player, warehouseHolder.getInventory());
    }

    public static void pageInventory(Player player, AuctionHousePageHolder paginationHolder) {
        openInventoryAndRegisterEvent(player, paginationHolder.getInventory());
    }

    private static void openInventoryAndRegisterEvent(Player player, Inventory inventory) {
        player.openInventory(inventory);
        Listener listener = registerInventoryClickEvent(player.getUniqueId(), inventory);
        listenerMap.put(player.getUniqueId(), listener);
        registerInventoryCloseEvent(player.getUniqueId());
    }

    private static Listener registerInventoryClickEvent(UUID uuid, Inventory inventory) {
        Server server = AuctionHouse.getInstance().getServer();
        WarehouseInventory warehouseInventory = new WarehouseInventory(inventory);
        Listener listener = new Listener() {};

        server.getPluginManager().registerEvent(InventoryClickEvent.class, listener, EventPriority.LOWEST, (listeners, event) -> {
            if (event instanceof InventoryClickEvent) {
                InventoryClickEvent clickEvent = (InventoryClickEvent) event;
                if (uuid.equals(clickEvent.getWhoClicked().getUniqueId()))
                    warehouseInventory.onClick(clickEvent);
            }
        }, AuctionHouse.getInstance());

        return listener;
    }

    private static void registerInventoryCloseEvent(UUID uuid) {
        Server server = AuctionHouse.getInstance().getServer();
        Listener closeEventListener = new Listener() {
        };

        server.getPluginManager().registerEvent(InventoryCloseEvent.class, closeEventListener, EventPriority.LOWEST, (listeners, event) -> {
            if (event instanceof InventoryCloseEvent) {
                InventoryCloseEvent closeEvent = (InventoryCloseEvent) event;
                if (uuid.equals(closeEvent.getPlayer().getUniqueId())) {
                    Listener listener = listenerMap.remove(uuid);
                    if (listener != null) {
                        InventoryClickEvent.getHandlerList().unregister(listener);
                    }
                    InventoryCloseEvent.getHandlerList().unregister(closeEventListener);
                }
            }
        }, AuctionHouse.getInstance());
    }
}
