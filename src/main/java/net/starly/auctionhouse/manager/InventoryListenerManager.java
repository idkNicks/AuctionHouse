package net.starly.auctionhouse.manager;

import net.starly.auctionhouse.page.PaginationHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.Server;
import net.starly.auctionhouse.AuctionHouse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class InventoryListenerManager {

    protected static final Map<UUID, Listener> listenerMap = new HashMap<>();
    protected abstract void onClick(InventoryClickEvent event);
    public void onClose(InventoryCloseEvent event) {}

    public abstract void openInventory(Player player);

    public abstract void pageInventory(Player player, PaginationHolder paginationHolder);

    protected void openInventoryAndRegisterEvent(Player player, Inventory inventory) {
        player.openInventory(inventory);
        Listener listener = registerInventoryClickEvent(player.getUniqueId(), inventory);
        listenerMap.put(player.getUniqueId(), listener);
        registerInventoryCloseEvent(player.getUniqueId(), this);
    }

    protected void registerInventoryCloseEvent(UUID uuid, InventoryListenerManager listenerManager) {
        Server server = AuctionHouse.getInstance().getServer();
        Listener closeEventListener = new Listener() {};

        server.getPluginManager().registerEvent(InventoryCloseEvent.class, closeEventListener, EventPriority.LOWEST, (listeners, event) -> {
            if (event instanceof InventoryCloseEvent) {
                InventoryCloseEvent closeEvent = (InventoryCloseEvent) event;
                if (uuid.equals(closeEvent.getPlayer().getUniqueId())) {
                    Listener listener = listenerMap.remove(uuid);
                    if (listener != null) {
                        InventoryClickEvent.getHandlerList().unregister(listener);
                    }
                    HandlerList.unregisterAll(closeEventListener);

                    listenerManager.onClose(closeEvent);
                }
            }
        }, AuctionHouse.getInstance());
    }

    protected Listener registerInventoryClickEvent(UUID uuid, Inventory inventory) {
        Server server = AuctionHouse.getInstance().getServer();
        Listener listener = new Listener() {};

        server.getPluginManager().registerEvent(InventoryClickEvent.class, listener, EventPriority.LOWEST, (listeners, event) -> {
            if (event instanceof InventoryClickEvent) {
                InventoryClickEvent clickEvent = (InventoryClickEvent) event;
                if (uuid.equals(clickEvent.getWhoClicked().getUniqueId()))
                    onClick(clickEvent);
            }
        }, AuctionHouse.getInstance());

        return listener;
    }
}