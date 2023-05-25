package net.starly.auctionhouse.manager;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.entity.impl.AuctionItem;
import net.starly.auctionhouse.inventory.AuctionHouseInventory;
import net.starly.auctionhouse.page.AuctionHousePageHolder;
import net.starly.auctionhouse.page.PaginationManager;
import net.starly.auctionhouse.storage.AuctionItemStorage;
import net.starly.auctionhouse.builder.ItemBuilder;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuctionHouseListenerManager {

    private static final Map<UUID, Listener> listenerMap = new HashMap<>();
    private static DecimalFormat priceFormat = new DecimalFormat("#,##0");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시mm분ss초");

    public static void openAuctionHouse(Player player) {
        List<AuctionItem> items = AuctionItemStorage.loadItems();
        for (AuctionItem item : items) {
            UUID sellerUuid = item.sellerId();
            long price = item.price();
            LocalDateTime expirationTime = item.expiryTime();

            new ItemBuilder(item.itemStack())
                    .setLore(List.of(
                            " §6§l판매자 | §f " + AuctionHouse.getInstance().getServer().getPlayer(sellerUuid).getDisplayName(),
                            " §6§l가격 | §f " + priceFormat.format(price),
                            " §6§l만료 시간 | §f" + dateFormatter.format(expirationTime),
                            " §6§l아이템스택 | §f " + item.itemStack().getType()
                    )).build();

            PaginationManager<AuctionItem> paginationManager = new PaginationManager<>(items);
            AuctionHousePageHolder<AuctionItem> paginationInventoryHolder = new AuctionHousePageHolder<>(paginationManager, paginationManager.toWarehousePaginationManager(), 50, 48, 45);

            openInventoryAndRegisterEvent(player, paginationInventoryHolder.getInventory());
        }
    }

    public static void pageInventory(Player player, AuctionHousePageHolder<AuctionItem> paginationHolder) {
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
                    HandlerList.unregisterAll(closeEventListener);
                }
            }
        }, AuctionHouse.getInstance());
    }
}