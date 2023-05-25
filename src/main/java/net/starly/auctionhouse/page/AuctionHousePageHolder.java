package net.starly.auctionhouse.page;

import lombok.Getter;
import net.starly.auctionhouse.builder.ItemBuilder;
import net.starly.auctionhouse.entity.AuctionItemOrStack;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
        Inventory inventory = createInventory("거래소 [" + getCurrentPage() + "]");

        try {
            AuctionHousePage<T> currentPage = paginationManager.getCurrentPageData();

            for (int i = 0; i < currentPage.itemStacks().size(); i++) {
                inventory.setItem(i, currentPage.itemStacks().get(i).getItemStack());
            }

        } catch (IndexOutOfBoundsException ignored) {}

        ItemStack warehouseItem = new ItemBuilder(Material.CHEST)
                .setName("만료된 아이템")
                .build();

        inventory.setItem(warehouseButtonSlot, warehouseItem);
        inventory.setItem(50, createNextPageItem());
        inventory.setItem(48, createPrevPageItem());

        return inventory;
    }
}
