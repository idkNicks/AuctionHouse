package net.starly.auctionhouse.manager;

import net.milkbowl.vault.economy.Economy;
import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.context.MessageContent;
import net.starly.auctionhouse.context.MessageType;
import net.starly.auctionhouse.entity.impl.AuctionItem;
import net.starly.auctionhouse.page.AuctionHousePage;
import net.starly.auctionhouse.page.AuctionHousePageHolder;
import net.starly.auctionhouse.page.PaginationManager;
import net.starly.auctionhouse.storage.AuctionItemStorage;
import net.starly.auctionhouse.builder.ItemBuilder;
import net.starly.auctionhouse.util.PaginationItemUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuctionHouseInventoryManager extends InventoryListenerBase {

    private static AuctionHouseInventoryManager instance;

    private AuctionHouseInventoryManager() {
    }

    public static AuctionHouseInventoryManager getInstance() {
        if (instance == null) instance = new AuctionHouseInventoryManager();
        return instance;
    }

    private static DecimalFormat priceFormat = new DecimalFormat("#,##0");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시mm분ss초");
    private MessageContent content = MessageContent.getInstance();

    @Override
    protected void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        event.setCancelled(true);

        if (!(event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        if (!(inventory.getHolder() instanceof AuctionHousePageHolder)) {
            if (event.getSlot() == 45) {
                WarehouseInventoryManager warehouseListenerManager = WarehouseInventoryManager.getInstance();
                warehouseListenerManager.openInventory(player);
                player.playSound(player.getLocation(),
                        Sound.valueOf(content.getMessage(MessageType.AUCTIONHOUSE, "items.warehouse.sound.name").orElse("ITEM_BOOK_PAGE_TURN")),
                        content.getFloat(MessageType.AUCTIONHOUSE, "items.warehouse.sound.volume"),
                        content.getFloat(MessageType.AUCTIONHOUSE, "items.warehouse.sound.pitch"));
            }
            return;
        }

        AuctionHousePageHolder paginationHolder = (AuctionHousePageHolder) inventory.getHolder();
        PaginationManager paginationManager = paginationHolder.getPaginationManager();

        if (event.getSlot() == paginationHolder.getPrevButtonSlot()) {
            if (!paginationManager.hasPrevPage()) return;
            paginationManager.prevPage();
            pageInventory(player, paginationHolder);
            player.playSound(player.getLocation(),
                    Sound.valueOf(content.getMessage(MessageType.AUCTIONHOUSE, "items.prevPage.sound.name").orElse("ITEM_BOOK_PAGE_TURN")),
                    content.getFloat(MessageType.AUCTIONHOUSE, "items.prevPage.sound.volume"),
                    content.getFloat(MessageType.AUCTIONHOUSE, "items.prevPage.sound.pitch"));
        } else if (event.getSlot() == paginationHolder.getNextButtonSlot()) {
            if (!paginationManager.hasNextPage()) return;
            paginationManager.nextPage();
            pageInventory(player, paginationHolder);
            player.playSound(player.getLocation(),
                    Sound.valueOf(content.getMessage(MessageType.AUCTIONHOUSE, "items.prevPage.sound.name").orElse("ITEM_BOOK_PAGE_TURN")),
                    content.getFloat(MessageType.AUCTIONHOUSE, "items.prevPage.sound.volume"),
                    content.getFloat(MessageType.AUCTIONHOUSE, "items.prevPage.sound.pitch"));
        } else if (event.getSlot() == paginationHolder.getWarehouseButtonSlot()) {
            WarehouseInventoryManager warehouseListenerManager = WarehouseInventoryManager.getInstance();
            warehouseListenerManager.openInventory(player);
            player.playSound(player.getLocation(),
                    Sound.valueOf(content.getMessage(MessageType.AUCTIONHOUSE, "items.warehouse.sound.name").orElse("ITEM_BOOK_PAGE_TURN")),
                    content.getFloat(MessageType.AUCTIONHOUSE, "items.warehouse.sound.volume"),
                    content.getFloat(MessageType.AUCTIONHOUSE, "items.warehouse.sound.pitch"));
        } else {
            Economy economy = AuctionHouse.getEconomy();
            int clickedSlot = event.getSlot();

            AuctionHousePage<AuctionItem> currentPage = paginationManager.getCurrentPageData();
            AuctionItem clickedItem = currentPage.getItemStacks().get(clickedSlot);

            if (clickedItem == null) return;

            List<AuctionItem> itemsInDb = AuctionItemStorage.loadItems();
            AuctionItem actualItem = itemsInDb
                    .stream()
                    .filter(item ->
                            item.sellerId().equals(clickedItem.sellerId())
                                    && item.price() == clickedItem.price()
                                    && item.expiryTime().isEqual(clickedItem.expiryTime())
                                    && areItemStacksEqual(item.itemStack(), clickedItem.itemStack())).findFirst().orElse(null);

            if (actualItem == null) {
                content.getMessageAfterPrefix(MessageType.ERROR, "itemUnavailable").ifPresent(player::sendMessage);
                player.playSound(player.getLocation(),
                        Sound.valueOf(content.getMessage(MessageType.OTHER, "errorSound.name").orElse("BLOCK_ANVIL_PLACE")),
                        content.getFloat(MessageType.OTHER, "errorSound.volume"),
                        content.getFloat(MessageType.OTHER, "errorSound.pitch"));
                return;
            }

            UUID buyerId = player.getUniqueId();

            if (actualItem != null && actualItem.sellerId().equals(buyerId)) {
                content.getMessageAfterPrefix(MessageType.ERROR, "noPurchaseOwnItem").ifPresent(player::sendMessage);
                player.playSound(player.getLocation(),
                        Sound.valueOf(content.getMessage(MessageType.OTHER, "errorSound.name").orElse("BLOCK_ANVIL_PLACE")),
                        content.getFloat(MessageType.OTHER, "errorSound.volume"),
                        content.getFloat(MessageType.OTHER, "errorSound.pitch"));
                return;
            }

            double playerBalance = economy.getBalance(player);
            if (playerBalance < actualItem.price()) {
                content.getMessageAfterPrefix(MessageType.ERROR, "notEnoughMoney").ifPresent(player::sendMessage);
                player.playSound(player.getLocation(),
                        Sound.valueOf(content.getMessage(MessageType.OTHER, "errorSound.name").orElse("BLOCK_ANVIL_PLACE")),
                        content.getFloat(MessageType.OTHER, "errorSound.volume"),
                        content.getFloat(MessageType.OTHER, "errorSound.pitch"));
                return;
            }

            if (!hasEmptyInventorySlot(player)) {
                content.getMessageAfterPrefix(MessageType.ERROR, "hasEmptyInventorySlot").ifPresent(player::sendMessage);
                player.playSound(player.getLocation(),
                        Sound.valueOf(content.getMessage(MessageType.OTHER, "errorSound.name").orElse("BLOCK_ANVIL_PLACE")),
                        content.getFloat(MessageType.OTHER, "errorSound.volume"),
                        content.getFloat(MessageType.OTHER, "errorSound.pitch"));
                return;
            }

            economy.withdrawPlayer(player, actualItem.price());
            player.getInventory().addItem(actualItem.itemStack());

            AuctionItemStorage.removeItem(actualItem);

            content.getMessageAfterPrefix(MessageType.NORMAL, "purchaseItem").ifPresent(player::sendMessage);
            player.playSound(player.getLocation(),
                    Sound.valueOf(content.getMessage(MessageType.OTHER, "completeSound.name").orElse("ENTITY_PLAYER_LEVELUP")),
                    content.getFloat(MessageType.OTHER, "completeSound.volume"),
                    content.getFloat(MessageType.OTHER, "completeSound.pitch"));

            player.closeInventory();

            AuctionHouse.getInstance().getServer().getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                OfflinePlayer seller = AuctionHouse.getInstance().getServer().getOfflinePlayer(actualItem.sellerId());
                if (seller.isOnline()) {
                    AuctionHouse.getInstance().getServer().getScheduler().runTask(AuctionHouse.getInstance(), () ->
                            content.getMessageAfterPrefix(MessageType.NORMAL, "notificationPurchaseItem").ifPresent(message -> seller.getPlayer().sendMessage(message)));
                }
                economy.depositPlayer(seller, actualItem.price());
            });

        }
    }

    private boolean areItemStacksEqual(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) return false;

        return item1.getType().equals(item2.getType()) && item1.getAmount() == item2.getAmount();
    }

    @Override
    public void openInventory(Player player) {
        List<AuctionItem> items = AuctionItemStorage.loadItems();

        if (items.isEmpty()) {
            Inventory inventory = AuctionHouse.getInstance().getServer().createInventory(null, 54, MessageContent.getInstance().getMessage(MessageType.AUCTIONHOUSE, "inventory.title").orElse(""));
            inventory.setItem(content.getInt(MessageType.AUCTIONHOUSE, "items.warehouse.slot"), PaginationItemUtil.createWarehouseItem());
            inventory.setItem(content.getInt(MessageType.AUCTIONHOUSE, "items.nextPage.slot"), PaginationItemUtil.createNextPageItem());
            inventory.setItem(content.getInt(MessageType.AUCTIONHOUSE, "items.prevPage.slot"), PaginationItemUtil.createPrevPageItem());
            openInventoryAndRegisterEvent(player, inventory);
            return;
        }

        PaginationManager<AuctionItem> paginationManager = new PaginationManager<>(items);
        AuctionHousePageHolder<AuctionItem> paginationInventoryHolder = new AuctionHousePageHolder<>(paginationManager, 50, 48, 45);

        for (AuctionItem item : items) {
            UUID sellerUuid = item.sellerId();
            long price = item.price();
            LocalDateTime expirationTime = item.expiryTime();

            new ItemBuilder(item.itemStack())
                    .setLore(content.getMessages(MessageType.AUCTIONHOUSE, "inventory.lore")
                            .stream()
                            .map(s -> s.replace("{seller}", AuctionHouse.getInstance().getServer().getOfflinePlayer(sellerUuid).getName())
                                    .replace("{price}", priceFormat.format(price))
                                    .replace("{expiry}", dateFormatter.format(expirationTime))
                                    .replace("{material}", item.itemStack().getType().name()))
                            .collect(Collectors.toList())).build();
        }
        openInventoryAndRegisterEvent(player, paginationInventoryHolder.getInventory());
    }
}
