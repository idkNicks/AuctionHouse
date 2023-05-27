package net.starly.auctionhouse.scheduler;

import net.starly.auctionhouse.entity.impl.AuctionItem;
import net.starly.auctionhouse.entity.impl.WarehouseItem;
import net.starly.auctionhouse.storage.AuctionItemStorage;
import net.starly.auctionhouse.storage.PlayerItemStorage;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.List;

public class AuctionItemExpiryScheduler extends BukkitRunnable {

    private final AuctionItemStorage itemStorage;

    public AuctionItemExpiryScheduler(AuctionItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Override
    public void run() {
        List<AuctionItem> items = itemStorage.loadItems();
        for (AuctionItem item : items) {
            if (item.expiryTime().isBefore(LocalDateTime.now())) {
                PlayerItemStorage.storeItem(item.sellerId(), new WarehouseItem(item.itemStack()));
                itemStorage.removeItem(item);
            }
        }
    }
}