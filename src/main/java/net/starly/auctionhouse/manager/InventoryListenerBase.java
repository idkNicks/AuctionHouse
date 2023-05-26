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

public abstract class InventoryListenerBase {

    protected static final Map<UUID, Listener> listenerMap = new HashMap<>();

    public void onClose(InventoryCloseEvent event) {}
    protected abstract void onClick(InventoryClickEvent event);
    public abstract void openInventory(Player player);

    protected void openInventoryAndRegisterEvent(Player player, Inventory inventory) {
        player.openInventory(inventory);
        Listener listener = registerInventoryClickEvent(player.getUniqueId());
        listenerMap.put(player.getUniqueId(), listener);
        registerInventoryCloseEvent(player.getUniqueId(), this);
    }

    protected void registerInventoryCloseEvent(UUID uuid, InventoryListenerBase listenerManager) {
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

    protected Listener registerInventoryClickEvent(UUID uuid) {
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

    public void pageInventory(Player player, PaginationHolder paginationHolder) {
        openInventoryAndRegisterEvent(player, paginationHolder.getInventory());
    }
}
