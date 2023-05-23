package net.starly.auctionhouse.auctionhouse.page;

import java.util.List;

public record AuctionHousePage(int pageNum, List<net.starly.auctionhouse.auctionhouse.entity.AuctionItem> itemStacks) {}
