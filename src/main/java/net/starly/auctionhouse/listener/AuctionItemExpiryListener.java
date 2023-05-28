package net.starly.auctionhouse.listener;

import net.starly.auctionhouse.entity.impl.AuctionItem;
import net.starly.auctionhouse.entity.impl.ExpiryItem;
import net.starly.auctionhouse.event.AuctionItemExpiryEvent;
import net.starly.auctionhouse.storage.AuctionItemStorage;
import net.starly.auctionhouse.storage.PlayerItemStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AuctionItemExpiryListener implements Listener {

    @EventHandler
    public void onAuctionItemExpiry(AuctionItemExpiryEvent event) {
        AuctionItem expiredItem = event.getExpiredItem();
        PlayerItemStorage.storeItem(expiredItem.sellerId(), new ExpiryItem(expiredItem.getItemStack()));
        AuctionItemStorage.removeItem(expiredItem);
    }
}

