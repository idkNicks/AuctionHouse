package net.starly.auctionhouse.page;

import lombok.Getter;
import lombok.Setter;
import net.starly.auctionhouse.entity.AuctionItemOrStack;
import net.starly.auctionhouse.entity.impl.WarehouseItem;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PaginationManager<T extends AuctionItemOrStack> {

    private final List<AuctionHousePage<T>> pages;
    private int currentPage;

    public PaginationManager(List<T> itemStacks) {
        this.pages = paginateItems(itemStacks);
        this.currentPage = 1;
    }

    public void nextPage() {
        if (hasNextPage()) currentPage++;
    }

    public void prevPage() {
        if (hasPrevPage()) currentPage--;
    }

    public boolean hasNextPage() { return currentPage < pages.size(); }

    public boolean hasPrevPage() { return currentPage > 1; }

    public AuctionHousePage<T> getCurrentPageData() { return pages.get(currentPage - 1); }

    public List<AuctionHousePage<T>> paginateItems(List<T> itemStacks) {
        final List<AuctionHousePage<T>> pages = new ArrayList<>();
        int itemCount = itemStacks.size();
        int pageCount = (int) Math.ceil((double) itemCount / 45);
        for (int i = 0; i < pageCount; i++) {
            int start = i * 45;
            int end = Math.min(start + 45, itemCount);
            List<T> pageItems = itemStacks.subList(start, end);
            pages.add(new AuctionHousePage<>(i + 1, pageItems));
        }
        return pages;
    }

    public PaginationManager<WarehouseItem> toWarehousePaginationManager() {
        List<WarehouseItem> warehouseItems = new ArrayList<>();
        for (AuctionHousePage<T> page : pages) {
            for (T item : page.itemStacks()) {
                if (item instanceof WarehouseItem) {
                    warehouseItems.add((WarehouseItem) item);
                }
            }
        }
        return new PaginationManager<>(warehouseItems);
    }
}
