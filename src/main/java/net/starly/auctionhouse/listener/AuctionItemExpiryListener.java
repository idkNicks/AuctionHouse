package net.starly.auctionhouse.listener;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.entity.AuctionItem;
import net.starly.auctionhouse.event.AuctionItemExpiryEvent;
import net.starly.auctionhouse.storage.AuctionItemStorage;
import net.starly.auctionhouse.storage.PlayerItemStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AuctionItemExpiryListener implements Listener {

    @EventHandler
    public void onAuctionItemExpiry(AuctionItemExpiryEvent event) {
        AuctionItem expiredItem = event.getExpiredItem();
        Player player = AuctionHouse.getInstance().getServer().getPlayer(expiredItem.sellerId());
        AuctionItemStorage.removeItem(expiredItem);
        player.sendMessage("거래소에 올려둔 아이템이 만료되었습니다.");
        if (player != null && player.isOnline()) {
            player.getInventory().addItem(expiredItem.itemStack());
        } else {
            PlayerItemStorage.storeItem(expiredItem.sellerId(), expiredItem.itemStack());
        }
    }
}

