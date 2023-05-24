package net.starly.auctionhouse.auctionhouse.page;

import net.starly.auctionhouse.AuctionHouse;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public record PaginationInventoryHolder(PaginationManager paginationManager, int nextButtonSlot, int prevButtonSlot) implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = AuctionHouse.getInstance().getServer().createInventory(this, 54, "유저거래소 [" + paginationManager.getCurrentPage() + "]");
        AuctionHousePage currentPage = paginationManager.getCurrentPageData();

        for (int i = 0; i < currentPage.itemStacks().size(); i++) {
            inventory.setItem(i, currentPage.itemStacks().get(i).itemStack());
        }

        ItemStack nextPageItem = new ItemStack(Material.ARROW);
        ItemMeta nextPageItemMeta = nextPageItem.getItemMeta();
        nextPageItemMeta.setDisplayName("다음 페이지");
        nextPageItem.setItemMeta(nextPageItemMeta);

        ItemStack prevPageItem = new ItemStack(Material.ARROW);
        ItemMeta prevPageItemMeta = prevPageItem.getItemMeta();
        prevPageItemMeta.setDisplayName("이전 페이지");
        prevPageItem.setItemMeta(prevPageItemMeta);

        inventory.setItem(nextButtonSlot, nextPageItem);
        inventory.setItem(prevButtonSlot, prevPageItem);

        return inventory;
    }
}
