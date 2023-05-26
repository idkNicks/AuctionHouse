package net.starly.auctionhouse.page;

import lombok.Getter;
import net.starly.auctionhouse.entity.AuctionItemOrStack;
import net.starly.auctionhouse.util.PaginationItemUtil;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

@Getter
public class AuctionHousePageHolder<T extends AuctionItemOrStack> extends PaginationHolder<T> {

    private final int warehouseButtonSlot;

    public AuctionHousePageHolder(PaginationManager<T> paginationManager, int nextButtonSlot, int prevButtonSlot, int warehouseButtonSlot) {
        super(paginationManager, nextButtonSlot, prevButtonSlot, "유저거래소");
        this.warehouseButtonSlot = warehouseButtonSlot;
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = createInventory("[" + getCurrentPage() + "]");

        AuctionHousePage<T> currentPage = paginationManager.getCurrentPageData();

        for (int i = 0; i < currentPage.getItemStacks().size(); i++) {
            inventory.setItem(i, currentPage.getItemStacks().get(i).getItemStack());
        }

        inventory.setItem(warehouseButtonSlot, PaginationItemUtil.createWarehouseItem());
        inventory.setItem(50, PaginationItemUtil.createNextPageItem());
        inventory.setItem(48, PaginationItemUtil.createPrevPageItem());

        return inventory;
    }
}
