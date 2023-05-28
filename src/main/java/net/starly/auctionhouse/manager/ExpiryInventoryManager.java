package net.starly.auctionhouse.manager;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.builder.ItemBuilder;
import net.starly.auctionhouse.context.MessageContent;
import net.starly.auctionhouse.context.MessageType;
import net.starly.auctionhouse.entity.impl.ExpiryItem;
import net.starly.auctionhouse.page.PaginationManager;
import net.starly.auctionhouse.page.ExpiryPageHolder;
import net.starly.auctionhouse.storage.PlayerItemStorage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("all")

public class ExpiryInventoryManager extends InventoryListenerBase {

    private static ExpiryInventoryManager instance;
    private MessageContent content = MessageContent.getInstance();

    private ExpiryInventoryManager() {}

    public static ExpiryInventoryManager getInstance() {
        if (instance == null) instance = new ExpiryInventoryManager();
        return instance;
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if (!(inventory.getHolder() instanceof ExpiryPageHolder<?>)) return;

        ExpiryPageHolder expiryPageHolder = (ExpiryPageHolder) inventory.getHolder();
        PaginationManager<ExpiryItem> paginationManager = expiryPageHolder.getPaginationManager();

        if (event.getSlot() == expiryPageHolder.getPrevButtonSlot()) {
            if (!paginationManager.hasPrevPage()) return;
            paginationManager.prevPage();
            pageInventory(player, expiryPageHolder);
            player.playSound(player.getLocation(),
                    Sound.valueOf(content.getMessage(MessageType.EXPIRY, "items.prevPage.sound.name").orElse("ITEM_BOOK_PAGE_TURN")),
                    content.getFloat(MessageType.EXPIRY, "items.prevPage.sound.volume"),
                    content.getFloat(MessageType.EXPIRY, "items.prevPage.sound.pitch"));
        }

        else if (event.getSlot() == expiryPageHolder.getNextButtonSlot()) {
            if (!paginationManager.hasNextPage()) return;
            paginationManager.nextPage();
            pageInventory(player, expiryPageHolder);
            player.playSound(player.getLocation(),
                    Sound.valueOf(content.getMessage(MessageType.EXPIRY, "items.nextPage.sound.name").orElse("ITEM_BOOK_PAGE_TURN")),
                    content.getFloat(MessageType.EXPIRY, "items.nextPage.sound.volume"),
                    content.getFloat(MessageType.EXPIRY, "items.nextPage.sound.pitch"));
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
            List<ExpiryItem> items = new ArrayList<>(paginationManager.getCurrentPageData().getItemStacks());
            if (clickedSlot >= 0 && clickedSlot < items.size()) {
                ExpiryItem clickedItem = items.get(clickedSlot);
                player.getInventory().addItem(clickedItem.itemStack());
                PlayerItemStorage.removeItem(player.getUniqueId(), clickedItem);

                content.getMessageAfterPrefix(MessageType.NORMAL, "getExpiryItem").ifPresent(player::sendMessage);
                player.playSound(player.getLocation(),
                        Sound.valueOf(content.getMessage(MessageType.OTHER, "completeSound.name").orElse("ENTITY_PLAYER_LEVELUP")),
                        content.getFloat(MessageType.OTHER, "completeSound.volume"),
                        content.getFloat(MessageType.OTHER, "completeSound.pitch"));

                Iterator<ExpiryItem> iterator = items.iterator();
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
                pageInventory(player, expiryPageHolder);
            }
        }
    }

    @Override
    public void openInventory(Player player) {
        List<ExpiryItem> items = PlayerItemStorage.loadExpiredItem(player.getUniqueId());

        PaginationManager<ExpiryItem> paginationManager = new PaginationManager<>(items);
        ExpiryPageHolder<ExpiryItem> paginationInventoryHolder = new ExpiryPageHolder<>(player.getUniqueId(), paginationManager, 50, 48);

        openInventoryAndRegisterEvent(player, paginationInventoryHolder.getInventory());
    }
}
