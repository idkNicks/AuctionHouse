package net.starly.auctionhouse.page;

import lombok.Getter;
import net.starly.auctionhouse.entity.AuctionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * PaginationManager 클래스는 아이템 목록의 페이징 처리를 담당합니다.
 * 각 페이지는 45개의 아이템을 보여주며, 이는 AuctionHousePage 객체를 통해 관리됩니다.
 *
 * @since 2023-05-23
 * @author idkNicks
 */
@Getter
public class PaginationManager {

    private final List<AuctionHousePage> pages;
    private int currentPage;

    public PaginationManager(List<AuctionItem> itemStacks) {
        this.pages = paginateItems(itemStacks);
        this.currentPage = 1;
    }

    public void nextPage() { if (hasNextPage()) currentPage++; }

    public void prevPage() { if (hasPrevPage()) currentPage--; }

    public boolean hasNextPage() { return currentPage < pages.size(); }

    public boolean hasPrevPage() { return currentPage > 1; }

    public AuctionHousePage getCurrentPageData() { return pages.get(currentPage - 1); }

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
