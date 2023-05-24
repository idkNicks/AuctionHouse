package net.starly.auctionhouse.page;

import lombok.Getter;
import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.builder.ItemBuilder;
import net.starly.auctionhouse.entity.AuctionItemOrStack;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class PaginationHolder<T extends AuctionItemOrStack> implements InventoryHolder {

    protected final PaginationManager<T> paginationManager;
    protected final int nextButtonSlot;
    protected final int prevButtonSlot;
    private final String title;

    public PaginationHolder(PaginationManager<T> paginationManager, int nextButtonSlot, int prevButtonSlot, String title) {
        this.paginationManager = paginationManager;
        this.nextButtonSlot = nextButtonSlot;
        this.prevButtonSlot = prevButtonSlot;
        this.title = title;
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = createInventory("[" + paginationManager.getCurrentPage() + "]");
        AuctionHousePage<T> currentPage = paginationManager.getCurrentPageData();

        for (int i = 0; i < currentPage.itemStacks().size(); i++) {
            inventory.setItem(i, currentPage.itemStacks().get(i).getItemStack());
        }

        inventory.setItem(nextButtonSlot, createNextPageItem());
        inventory.setItem(prevButtonSlot, createPrevPageItem());

        return inventory;
    }

    protected @NotNull Inventory createInventory(String pageName) {
        return AuctionHouse.getInstance().getServer().createInventory(this, 54, title + " " + pageName);
    }

    protected int getCurrentPage() {
        return paginationManager.getCurrentPage();
    }

    protected @NotNull ItemStack createNextPageItem() {
        return new ItemBuilder(Material.ARROW)
                .setName("다음 페이지")
                .build();
    }

    protected @NotNull ItemStack createPrevPageItem() {
        return new ItemBuilder(Material.ARROW)
                .setName("이전 페이지")
                .build();
    }
}
