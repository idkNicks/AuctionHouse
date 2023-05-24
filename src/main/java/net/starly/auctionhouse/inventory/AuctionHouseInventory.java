package net.starly.auctionhouse.inventory;

import lombok.AllArgsConstructor;
import net.starly.auctionhouse.page.PaginationInventoryHolder;
import net.starly.auctionhouse.page.PaginationManager;
import net.starly.auctionhouse.manager.AuctionHouseListenerManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * AuctionHouseInventory 클래스는 경매하우스 인벤토리의 클릭 이벤트를 처리합니다.
 *
 * @since 2023-05-24
 * @author idkNicks
 */
@AllArgsConstructor
public class AuctionHouseInventory {

    private final Inventory inventory;

    public void onClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        PaginationInventoryHolder paginationHolder = (PaginationInventoryHolder) inventory.getHolder();
        PaginationManager paginationManager = paginationHolder.paginationManager();

        if (event.getSlot() == paginationHolder.prevButtonSlot()) {
            if (!paginationManager.hasPrevPage()) return;
            paginationManager.prevPage();
            AuctionHouseListenerManager.pageInventory(player, paginationHolder);
            player.playSound(player.getLocation(), Sound.valueOf("ITEM_BOOK_PAGE_TURN"), 2, 1);
        }

        if (event.getSlot() == paginationHolder.nextButtonSlot()) {
            if (!paginationManager.hasNextPage()) return;
            paginationManager.nextPage();
            AuctionHouseListenerManager.pageInventory(player, paginationHolder);
            player.playSound(player.getLocation(), Sound.valueOf("ITEM_BOOK_PAGE_TURN"), 2, 1);
        }
    }
}
