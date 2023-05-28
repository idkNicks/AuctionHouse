package net.starly.auctionhouse.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {

    ERROR("errorMessages"),
    NORMAL("messages"),
    AUCTIONHOUSE("auctionHouse"),
    EXPIRY("expiry"),
    WAREHOUSE("warehouse"),
    CONFIRMGUI("confirmInventory"),
    OTHER("other");

    public final String key;
}
