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

    /**
     * 주어진 아이템 목록을 페이징하여 PaginationManager를 초기화합니다.
     * @param itemStacks 페이징 할 아이템 목록
     */
    public PaginationManager(List<AuctionItem> itemStacks) {
        this.pages = paginateItems(itemStacks);
        this.currentPage = 1;
    }

    /**
     * 다음 페이지로 이동합니다. 이미 마지막 페이지라면 아무런 동작을 하지 않습니다.
     */
    public void nextPage() { if (hasNextPage()) currentPage++; }

    /**
     * 이전 페이지로 이동합니다. 이미 첫 페이지라면 아무런 동작을 하지 않습니다.
     */
    public void prevPage() { if (hasPrevPage()) currentPage--; }

    /**
     * 다음 페이지가 존재하는지 확인합니다.
     * @return 다음 페이지가 존재하면 true, 그렇지 않으면 false
     */
    public boolean hasNextPage() { return currentPage < pages.size(); }

    /**
     * 이전 페이지가 존재하는지 확인합니다.
     * @return 이전 페이지가 존재하면 true, 그렇지 않으면 false
     */
    public boolean hasPrevPage() { return currentPage > 1; }

    /**
     * 현재 페이지의 데이터를 가져옵니다.
     * @return 현재 페이지의 AuctionHousePage 객체
     */
    public AuctionHousePage getCurrentPageData() { return pages.get(currentPage - 1); }

    /**
     * 주어진 아이템 목록을 페이지로 분할합니다. 각 페이지는 최대 45개의 아이템을 가집니다.
     * @param itemStacks 페이지로 분할할 아이템 목록
     * @return 분할된 페이지 목록
     */
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
