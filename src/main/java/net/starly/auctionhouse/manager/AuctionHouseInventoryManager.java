package net.starly.auctionhouse.manager;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.entity.impl.AuctionItem;
import net.starly.auctionhouse.page.AuctionHousePageHolder;
import net.starly.auctionhouse.page.PaginationManager;
import net.starly.auctionhouse.storage.AuctionItemStorage;
import net.starly.auctionhouse.builder.ItemBuilder;
import net.starly.auctionhouse.util.PaginationItemUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class AuctionHouseInventoryManager extends InventoryListenerBase {

    private static AuctionHouseInventoryManager instance;

    private AuctionHouseInventoryManager() {
    }

    public static AuctionHouseInventoryManager getInstance() {
        if (instance == null) instance = new AuctionHouseInventoryManager();
        return instance;
    }

    private static DecimalFormat priceFormat = new DecimalFormat("#,##0");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시mm분ss초");

    @Override
    protected void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if (!(inventory.getHolder() instanceof AuctionHousePageHolder)) {
            if (event.getSlot() == 45) {
                WarehouseInventoryManager warehouseListenerManager = WarehouseInventoryManager.getInstance();
                warehouseListenerManager.openInventory(player);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2, 1);
            }
            return;
        }

        AuctionHousePageHolder paginationHolder = (AuctionHousePageHolder) inventory.getHolder();
        PaginationManager paginationManager = paginationHolder.getPaginationManager();

        if (event.getSlot() == paginationHolder.getPrevButtonSlot()) {
            if (!paginationManager.hasPrevPage()) return;
            paginationManager.prevPage();
            pageInventory(player, paginationHolder);
            player.playSound(player.getLocation(), Sound.valueOf("ITEM_BOOK_PAGE_TURN"), 2, 1);
        } else if (event.getSlot() == paginationHolder.getNextButtonSlot()) {
            if (!paginationManager.hasNextPage()) return;
            paginationManager.nextPage();
            pageInventory(player, paginationHolder);
            player.playSound(player.getLocation(), Sound.valueOf("ITEM_BOOK_PAGE_TURN"), 2, 1);
        } else if (event.getSlot() == paginationHolder.getWarehouseButtonSlot()) {
            WarehouseInventoryManager warehouseListenerManager = WarehouseInventoryManager.getInstance();
            warehouseListenerManager.openInventory(player);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2, 1);
        }
    }

    @Override
    public void openInventory(Player player) {
        List<AuctionItem> items = AuctionItemStorage.loadItems();

        if (items.isEmpty()) {
            Inventory inventory = AuctionHouse.getInstance().getServer().createInventory(null, 54, "유저거래소 [1]");
            inventory.setItem(45, PaginationItemUtil.createWarehouseItem());
            inventory.setItem(50, PaginationItemUtil.createNextPageItem());
            inventory.setItem(48, PaginationItemUtil.createPrevPageItem());
            openInventoryAndRegisterEvent(player, inventory);
            return;
        }

        PaginationManager<AuctionItem> paginationManager = new PaginationManager<>(items);
        AuctionHousePageHolder<AuctionItem> paginationInventoryHolder = new AuctionHousePageHolder<>(paginationManager, 50, 48, 45);

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
        }
        openInventoryAndRegisterEvent(player, paginationInventoryHolder.getInventory());
    }
}
