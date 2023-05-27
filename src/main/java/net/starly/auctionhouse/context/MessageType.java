package net.starly.auctionhouse.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {

    ERROR("errorMessages"),
    NORMAL("messages"),
    AUCTIONHOUSE("auctionHouse"),
    WAREHOUSE("warehouse"),
    OTHER("other");

    public final String key;
}
