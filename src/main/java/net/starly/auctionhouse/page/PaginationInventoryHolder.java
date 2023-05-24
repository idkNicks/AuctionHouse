package net.starly.auctionhouse.page;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record PaginationInventoryHolder(PaginationManager paginationManager, int nextButtonSlot, int prevButtonSlot) implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = AuctionHouse.getInstance().getServer().createInventory(this, 54, "유저거래소 [" + paginationManager.getCurrentPage() + "]");
        AuctionHousePage currentPage = paginationManager.getCurrentPageData();

        for (int i = 0; i < currentPage.itemStacks().size(); i++) {
            inventory.setItem(i, currentPage.itemStacks().get(i).itemStack());
        }

        ItemStack nextPageItem = new ItemBuilder(Material.ARROW)
                .setName("다음 페이지")
                .build();

        ItemStack prevPageItem = new ItemBuilder(Material.ARROW)
                .setName("이전 페이지")
                .build();

        inventory.setItem(nextButtonSlot, nextPageItem);
        inventory.setItem(prevButtonSlot, prevPageItem);

        return inventory;
    }
}
