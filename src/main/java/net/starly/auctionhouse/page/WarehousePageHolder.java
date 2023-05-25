package net.starly.auctionhouse.page;

import lombok.Getter;
import net.starly.auctionhouse.entity.AuctionItemOrStack;
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

            for (int i = 0; i < currentPage.itemStacks().size(); i++) {
                inventory.setItem(i, currentPage.itemStacks().get(i).getItemStack());
            }

        } catch (IndexOutOfBoundsException ignored) {}

        inventory.setItem(50, createNextPageItem());
        inventory.setItem(48, createPrevPageItem());

        return inventory;
    }
}
