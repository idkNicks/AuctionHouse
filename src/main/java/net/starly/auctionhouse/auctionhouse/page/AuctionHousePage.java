package net.starly.auctionhouse.auctionhouse.page;

import net.starly.auctionhouse.auctionhouse.entity.AuctionItem;

import java.util.List;

public record AuctionHousePage(int pageNum, List<AuctionItem> itemStacks) {}
