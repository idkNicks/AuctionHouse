package net.starly.auctionhouse.page;

import lombok.Getter;
import net.starly.auctionhouse.builder.ItemBuilder;
import net.starly.auctionhouse.context.MessageContent;
import net.starly.auctionhouse.context.MessageType;
import net.starly.auctionhouse.entity.AuctionItemOrStack;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class WarehousePageHolder<T extends AuctionItemOrStack> extends PaginationHolder<T> {

    private final UUID uuid;
    private MessageContent content = MessageContent.getInstance();

    public WarehousePageHolder(UUID uuid, PaginationManager<T> paginationManager, int nextButtonSlot, int prevButtonSlot) {
        super(paginationManager, nextButtonSlot, prevButtonSlot, MessageContent.getInstance().getMessage(MessageType.WAREHOUSE, "inventory.title").orElse(""));
        this.uuid = uuid;
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = createInventory("");

        try {
            AuctionHousePage<T> currentPage = paginationManager.getCurrentPageData();

            for (int i = 0; i < currentPage.getItemStacks().size(); i++) {
                inventory.setItem(i, currentPage.getItemStacks().get(i).getItemStack());
            }
        } catch (IndexOutOfBoundsException ignored) {}

        inventory.setItem(content.getInt(MessageType.WAREHOUSE, "items.nextPage.slot"), createNextPageItem());
        inventory.setItem(content.getInt(MessageType.WAREHOUSE, "items.prevPage.slot"), createPrevPageItem());

        return inventory;
    }

    private @NotNull ItemStack createNextPageItem() {
        return new ItemBuilder(Material.valueOf(content.getMessage(MessageType.WAREHOUSE, "items.nextPage.material").orElse("ARROW")))
                .setName(content.getMessage(MessageType.WAREHOUSE, "items.nextPage.displayname").orElse(""))
                .setLore(content.getMessages(MessageType.WAREHOUSE, "items.nextPage.lore"))
                .build();
    }

    private @NotNull ItemStack createPrevPageItem() {
        return new ItemBuilder(Material.valueOf(content.getMessage(MessageType.WAREHOUSE, "items.prevPage.material").orElse("ARROW")))
                .setName(content.getMessage(MessageType.WAREHOUSE, "items.prevPage.displayname").orElse(""))
                .setLore(content.getMessages(MessageType.WAREHOUSE, "items.prevPage.lore"))
                .build();
    }
}
