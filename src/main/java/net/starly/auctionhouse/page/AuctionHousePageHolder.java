package net.starly.auctionhouse.page;

import lombok.Getter;
import net.starly.auctionhouse.context.MessageContent;
import net.starly.auctionhouse.context.MessageType;
import net.starly.auctionhouse.entity.AuctionItemOrStack;
import net.starly.auctionhouse.util.PaginationItemUtil;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

@Getter
public class AuctionHousePageHolder<T extends AuctionItemOrStack> extends PaginationHolder<T> {

    private final int warehouseButtonSlot;
    private MessageContent content = MessageContent.getInstance();

    public AuctionHousePageHolder(PaginationManager<T> paginationManager, int nextButtonSlot, int prevButtonSlot, int warehouseButtonSlot) {
        super(paginationManager, nextButtonSlot, prevButtonSlot, MessageContent.getInstance().getMessage(MessageType.AUCTIONHOUSE, "inventory.title").orElse(""));
        this.warehouseButtonSlot = warehouseButtonSlot;
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = createInventory("");

        AuctionHousePage<T> currentPage = paginationManager.getCurrentPageData();

        for (int i = 0; i < currentPage.getItemStacks().size(); i++) {
            inventory.setItem(i, currentPage.getItemStacks().get(i).getItemStack());
        }

        inventory.setItem(warehouseButtonSlot, PaginationItemUtil.createWarehouseItem());
        inventory.setItem(content.getInt(MessageType.AUCTIONHOUSE, "items.nextPage.slot"), PaginationItemUtil.createNextPageItem());
        inventory.setItem(content.getInt(MessageType.AUCTIONHOUSE, "items.prevPage.slot"), PaginationItemUtil.createPrevPageItem());

        return inventory;
    }
}
