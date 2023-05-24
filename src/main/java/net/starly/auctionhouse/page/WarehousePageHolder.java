package net.starly.auctionhouse.page;

import lombok.Getter;
import net.starly.auctionhouse.entity.impl.WarehouseItem;
import net.starly.auctionhouse.storage.PlayerItemStorage;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class WarehousePageHolder extends PaginationHolder<WarehouseItem> {

    private final UUID uuid;

    public WarehousePageHolder(UUID uuid, PaginationManager<WarehouseItem> paginationManager, int nextButtonSlot, int prevButtonSlot) {
        super(paginationManager, nextButtonSlot, prevButtonSlot, "만료아이템");
        this.uuid = uuid;
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = createInventory("[" + getCurrentPage() + "]");

        List<ItemStack> expiredItems = PlayerItemStorage.loadExpiredItem(uuid);
        List<WarehouseItem> warehouseItems = expiredItems.stream()
                .map(WarehouseItem::new)
                .collect(Collectors.toList());

        AuctionHousePage<WarehouseItem> page = new AuctionHousePage<>(1, warehouseItems);

        for (int i = 0; i < page.itemStacks().size(); i++) {
            inventory.setItem(i, page.itemStacks().get(i).getItemStack());
        }

        inventory.setItem(50, createNextPageItem());
        inventory.setItem(48, createPrevPageItem());

        return inventory;
    }
}
