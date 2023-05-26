package net.starly.auctionhouse.page;

import lombok.Getter;
import net.starly.auctionhouse.entity.AuctionItemOrStack;
import net.starly.auctionhouse.util.PaginationItemUtil;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class WarehousePageHolder<T extends AuctionItemOrStack> extends PaginationHolder<T> {

    private final UUID uuid;

    public WarehousePageHolder(UUID uuid, PaginationManager<T> paginationManager, int nextButtonSlot, int prevButtonSlot) {
        super(paginationManager, nextButtonSlot, prevButtonSlot, "만료아이템");
        this.uuid = uuid;
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = createInventory("[" + paginationManager.getCurrentPage() + "]");

        try {
            AuctionHousePage<T> currentPage = paginationManager.getCurrentPageData();

            for (int i = 0; i < currentPage.getItemStacks().size(); i++) {
                inventory.setItem(i, currentPage.getItemStacks().get(i).getItemStack());
            }
        } catch (IndexOutOfBoundsException ignored) {}

        inventory.setItem(50, PaginationItemUtil.createNextPageItem());
        inventory.setItem(48, PaginationItemUtil.createPrevPageItem());

        return inventory;
    }
}
