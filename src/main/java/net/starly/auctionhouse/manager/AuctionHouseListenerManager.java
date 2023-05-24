package net.starly.auctionhouse.manager;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.inventory.AuctionHouseInventory;
import net.starly.auctionhouse.entity.AuctionItem;
import net.starly.auctionhouse.page.PaginationInventoryHolder;
import net.starly.auctionhouse.page.PaginationManager;
import net.starly.auctionhouse.storage.AuctionHouseItemStorage;
import net.starly.auctionhouse.builder.ItemBuilder;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AuctionHouseListenerManager 클래스는 경매 아이템들의 상호작용을 관리합니다.
 * 이 클래스는 AuctionHouse에서 아이템 상호작용과 관련된 모든 기능을 제공합니다.
 *
 * @since 2023-05-23
 * @author idkNicks
 */
public class AuctionHouseListenerManager {

    /**
     * 인벤토리 클릭 이벤트 리스너를 관리하는 맵입니다. 플레이어의 UUID를 키로 사용합니다.
     */
    private static final Map<UUID, Listener> listenerMap = new HashMap<>();
    private static DecimalFormat priceFormat = new DecimalFormat("#,##0");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시mm분ss초");

    /**
     * 플레이어에게 경매장 인벤토리를 엽니다.
     * 이 메소드는 저장된 모든 경매 아이템을 로드하고, 이 아이템들을 인벤토리에 추가합니다.
     *
     * @param player 경매장 인벤토리를 열 플레이어
     */
    public static void openAuctionHouse(Player player) {
        List<AuctionItem> items = AuctionHouseItemStorage.loadItems();
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

            PaginationManager paginationManager = new PaginationManager(items);
            PaginationInventoryHolder paginationInventoryHolder = new PaginationInventoryHolder(paginationManager, 50, 48);

            openInventoryAndRegisterEvent(player, paginationInventoryHolder.getInventory());
        }
    }

    /**
     * 플레이어에게 주어진 PaginationInventoryHolder에 따른 인벤토리 페이지를 엽니다.
     *
     * @param player 경매장 인벤토리를 열 플레이어
     * @param paginationHolder 열려야 하는 페이지 정보를 가진 PaginationInventoryHolder
     */
    public static void pageInventory(Player player, PaginationInventoryHolder paginationHolder) {
        openInventoryAndRegisterEvent(player, paginationHolder.getInventory());
    }

    /**
     * 플레이어에게 주어진 인벤토리를 열고, 이벤트를 등록합니다.
     *
     * @param player 인벤토리를 열 플레이어
     * @param inventory 열려야 하는 인벤토리
     */
    private static void openInventoryAndRegisterEvent(Player player, Inventory inventory) {
        player.openInventory(inventory);
        Listener listener = registerInventoryClickEvent(player.getUniqueId(), inventory);
        listenerMap.put(player.getUniqueId(), listener);
        registerInventoryCloseEvent(player.getUniqueId());
    }

    /**
     * 인벤토리 클릭 이벤트를 등록합니다.
     * 등록된 이벤트는 플레이어가 클릭한 인벤토리 아이템에 대한 상호작용을 관리합니다.
     *
     * @param uuid 클릭 이벤트를 등록할 플레이어의 UUID
     * @param inventory 클릭 이벤트를 등록할 인벤토리
     * @return 등록된 클릭 이벤트 리스너
     */
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

    /**
     * 인벤토리 닫기 이벤트를 등록합니다.
     * 등록된 이벤트는 플레이어가 인벤토리를 닫았을 때, 클릭 이벤트 리스너를 해제합니다.
     *
     * @param uuid 닫기 이벤트를 등록할 플레이어의 UUID
     */
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