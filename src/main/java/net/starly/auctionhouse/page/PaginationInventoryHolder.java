package net.starly.auctionhouse.page;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * PaginationInventoryHolder 클래스는 PaginationManager와 페이지 이동 버튼의 위치를 저장하는 레코드입니다.
 * 또한, InventoryHolder 인터페이스를 구현하여 해당 인벤토리를 관리합니다.
 *
 * @param paginationManager  페이지 정보를 관리하는 PaginationManager 인스턴스
 * @param nextButtonSlot     다음 페이지로 이동하는 버튼이 위치할 인벤토리 슬롯 번호
 * @param prevButtonSlot     이전 페이지로 이동하는 버튼이 위치할 인벤토리 슬롯 번호
 *
 * @since 2023-05-23
 * @author idkNicks
 */
public record PaginationInventoryHolder(PaginationManager paginationManager, int nextButtonSlot, int prevButtonSlot) implements InventoryHolder {

    /**
     * 인벤토리를 가져오는 메소드입니다.
     * 여기에서는 현재 페이지의 아이템을 인벤토리에 설정하고, 이전/다음 페이지 버튼을 추가합니다.
     *
     * @return 생성된 인벤토리
     */
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
