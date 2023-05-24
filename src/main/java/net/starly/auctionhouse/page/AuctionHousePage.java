
package net.starly.auctionhouse.page;

import net.starly.auctionhouse.entity.AuctionItemOrStack;

import java.util.List;

public record AuctionHousePage<T extends AuctionItemOrStack>(int pageNum, List<T> itemStacks) {}
