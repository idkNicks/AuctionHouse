package net.starly.auctionhouse.auctionhouse.page;

import lombok.Getter;
import net.starly.auctionhouse.auctionhouse.entity.AuctionItem;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PaginationManager {

    private final List<AuctionHousePage> pages;
    private int currentPage;

    public PaginationManager(List<AuctionItem> itemStacks) {
        this.pages = paginateItems(itemStacks);
        this.currentPage = 1;
    }

    public void nextPage() {
        if (currentPage < pages.size()) currentPage++;
    }

    public void prevPage() {
        if (currentPage > 1) currentPage--;
    }

    public AuctionHousePage getCurrentPageData() {
        return pages.get(currentPage - 1);
    }

    public List<AuctionHousePage> paginateItems(List<AuctionItem> itemStacks) {
        final List<AuctionHousePage> pages = new ArrayList<>();
        int itemCount = itemStacks.size();
        int pageCount = (int) Math.ceil((double) itemCount / 45);
        for (int i = 0; i < pageCount; i++) {
            int start = i * 45;
            int end = Math.min(start + 45, itemCount);
            List<AuctionItem> pagesItems = itemStacks.subList(start, end);
            pages.add(new AuctionHousePage(i + 1, pagesItems));
        }
        return pages;
    }
}
