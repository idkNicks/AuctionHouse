package net.starly.auctionhouse.auctionhouse.manager;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.auctionhouse.AuctionHouseInventory;
import net.starly.auctionhouse.auctionhouse.entity.AuctionItem;
import net.starly.auctionhouse.auctionhouse.page.PaginationInventoryHolder;
import net.starly.auctionhouse.auctionhouse.page.PaginationManager;
import net.starly.auctionhouse.auctionhouse.storage.AuctionHouseItemStorage;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuctionHouseListenerManager {

    private static final Map<UUID, Listener> listenerMap = new HashMap<>();

    public static void openAuctionHouse(Player player) {
        List<AuctionItem> items = AuctionHouseItemStorage.loadItems();
        for (AuctionItem item : items) {
            UUID sellerUuid = item.sellerId();
            ItemStack itemStack = item.itemStack();
            long price = item.price();
            LocalDateTime expirationTime = item.expiryTime();

            System.out.println("판매자 UUID: " + sellerUuid);
            System.out.println("가격: " + price);
            System.out.println("만료 시간: " + expirationTime);
            System.out.println("아이템 스택: " + itemStack);

            PaginationManager paginationManager = new PaginationManager(items);
            PaginationInventoryHolder paginationInventoryHolder = new PaginationInventoryHolder(paginationManager, 50, 48);

            openInventoryAndRegisterEvent(player, paginationInventoryHolder.getInventory());
        }
    }

    public static void pageInventory(Player player, PaginationInventoryHolder paginationHolder) {
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
        AuctionHouseInventory auctionHouseInventory = new AuctionHouseInventory(inventory);
        Listener listener = new Listener() {};

        server.getPluginManager().registerEvent(InventoryClickEvent.class, listener, EventPriority.LOWEST, (listeners, event) -> {
            if (event instanceof InventoryClickEvent) {
                InventoryClickEvent clickEvent = (InventoryClickEvent) event;
                if (uuid.equals(clickEvent.getWhoClicked().getUniqueId()))
                    auctionHouseInventory.onClick(clickEvent);
            }
        }, AuctionHouse.getInstance());

        return listener;
    }

    private static void registerInventoryCloseEvent(UUID uuid) {
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
                    InventoryCloseEvent.getHandlerList().unregister(closeEventListener);
                }
            }
        }, AuctionHouse.getInstance());
    }
}