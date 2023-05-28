package net.starly.auctionhouse.manager;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.builder.ItemBuilder;
import net.starly.auctionhouse.context.MessageContent;
import net.starly.auctionhouse.context.MessageType;
import net.starly.auctionhouse.entity.impl.AuctionItem;
import net.starly.auctionhouse.entity.impl.ExpiryItem;
import net.starly.auctionhouse.page.PaginationManager;
import net.starly.auctionhouse.page.WarehousePageHolder;
import net.starly.auctionhouse.storage.AuctionItemStorage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("all")

public class WarehouseManager extends InventoryListenerBase {

    private static WarehouseManager instance;

    public static WarehouseManager getInstance() {
        if (instance == null) instance = new WarehouseManager();
        return instance;
    }

    private WarehouseManager() {}

    private MessageContent content = MessageContent.getInstance();

    @Override
    protected void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if (!(inventory.getHolder() instanceof WarehousePageHolder<?>)) return;

        WarehousePageHolder warehousePageHolder = (WarehousePageHolder) inventory.getHolder();
        PaginationManager<AuctionItem> paginationManager = warehousePageHolder.getPaginationManager();

        if (event.getSlot() == warehousePageHolder.getPrevButtonSlot()) {
            if (!paginationManager.hasPrevPage()) return;
            paginationManager.prevPage();
            pageInventory(player, warehousePageHolder);
            player.playSound(player.getLocation(),
                    Sound.valueOf(content.getMessage(MessageType.WAREHOUSE, "items.prevPage.sound.name").orElse("ITEM_BOOK_PAGE_TURN")),
                    content.getFloat(MessageType.WAREHOUSE, "items.prevPage.sound.volume"),
                    content.getFloat(MessageType.WAREHOUSE, "items.prevPage.sound.pitch"));
        }

        else if (event.getSlot() == warehousePageHolder.getNextButtonSlot()) {
            if (!paginationManager.hasNextPage()) return;
            paginationManager.nextPage();
            pageInventory(player, warehousePageHolder);
            player.playSound(player.getLocation(),
                    Sound.valueOf(content.getMessage(MessageType.WAREHOUSE, "items.nextPage.sound.name").orElse("ITEM_BOOK_PAGE_TURN")),
                    content.getFloat(MessageType.WAREHOUSE, "items.nextPage.sound.volume"),
                    content.getFloat(MessageType.WAREHOUSE, "items.nextPage.sound.pitch"));
        }

        else {
            if (!hasEmptyInventorySlot(player)) {
                content.getMessageAfterPrefix(MessageType.ERROR, "hasEmptyInventorySlot").ifPresent(player::sendMessage);
                player.playSound(player.getLocation(),
                        Sound.valueOf(content.getMessage(MessageType.OTHER, "errorSound.name").orElse("BLOCK_ANVIL_PLACE")),
                        content.getFloat(MessageType.OTHER, "errorSound.volume"),
                        content.getFloat(MessageType.OTHER, "errorSound.pitch"));
                return;
            }

            int clickedSlot = event.getSlot();
            List<AuctionItem> items = new ArrayList<>(paginationManager.getCurrentPageData().getItemStacks());
            if (clickedSlot >= 0 && clickedSlot < items.size()) {
                AuctionItem clickedItem = items.get(clickedSlot);
                player.getInventory().addItem(clickedItem.itemStack());
                AuctionItemStorage.removeItem(clickedItem);

                content.getMessageAfterPrefix(MessageType.NORMAL, "removeAuctionhouseItem").ifPresent(player::sendMessage);
                player.playSound(player.getLocation(),
                        Sound.valueOf(content.getMessage(MessageType.OTHER, "completeSound.name").orElse("ENTITY_PLAYER_LEVELUP")),
                        content.getFloat(MessageType.OTHER, "completeSound.volume"),
                        content.getFloat(MessageType.OTHER, "completeSound.pitch"));

                Iterator<AuctionItem> iterator = items.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().equals(clickedItem)) {
                        iterator.remove();
                        break;
                    }
                }

                paginationManager.getCurrentPageData().setItemStacks(items);

                if (items.isEmpty() && paginationManager.hasPrevPage()) {
                    paginationManager.prevPage();
                }
                pageInventory(player, warehousePageHolder);
            }
        }
    }

    @Override
    public void openInventory(Player player) {
        List<AuctionItem> items = AuctionItemStorage.getItemsByPlayer(player.getUniqueId());

        if (items.isEmpty()) {
            Inventory inventory = AuctionHouse.getInstance().getServer().createInventory(null, 54, "아이템 목록");
            inventory.setItem(content.getInt(MessageType.WAREHOUSE, "items.nextPage.slot"), createNextPageItem());
            inventory.setItem(content.getInt(MessageType.WAREHOUSE, "items.prevPage.slot"), createPrevPageItem());
            openInventoryAndRegisterEvent(player, inventory);
            return;
        }

        PaginationManager<AuctionItem> paginationManager = new PaginationManager<>(items);
        WarehousePageHolder<AuctionItem> paginationInventoryHolder = new WarehousePageHolder<>(paginationManager, content.getInt(MessageType.WAREHOUSE, "items.nextPage.slot"), content.getInt(MessageType.WAREHOUSE, "items.prevPage.slot"));

        openInventoryAndRegisterEvent(player, paginationInventoryHolder.getInventory());
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
