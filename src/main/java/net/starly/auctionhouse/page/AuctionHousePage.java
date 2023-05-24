package net.starly.auctionhouse.page;

import net.starly.auctionhouse.entity.AuctionItem;

import java.util.List;

public record AuctionHousePage(int pageNum, List<AuctionItem> itemStacks) {}
