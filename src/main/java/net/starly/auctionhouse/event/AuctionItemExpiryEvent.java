package net.starly.auctionhouse.event;

import lombok.Getter;
import net.starly.auctionhouse.entity.impl.AuctionItem;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AuctionItemExpiryEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter private final AuctionItem expiredItem;

    public AuctionItemExpiryEvent(AuctionItem expiredItem) {
        this.expiredItem = expiredItem;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
