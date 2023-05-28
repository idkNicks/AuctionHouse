package net.starly.auctionhouse.storage;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.entity.impl.AuctionItem;
import net.starly.auctionhouse.util.ItemSerializationUtil;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionItemStorage {

    private static final File FILE = new File(AuctionHouse.getInstance().getDataFolder(), "auctionhouse.db");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시mm분ss초");

    public static void storeItem(UUID sellerUuid, long price, LocalDateTime expirationTime, ItemStack itemStack, int amount) {
        itemStack.setAmount(amount);

        String serializedItemStack = ItemSerializationUtil.serializeItemStack(itemStack);
        String line = sellerUuid + "," + price + "," + expirationTime.format(DATE_FORMATTER) + "," + serializedItemStack;

        if (!FILE.exists()) {
            try {
                FILE.createNewFile();
            } catch (IOException e) { e.printStackTrace(); }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(FILE.toPath(), StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void removeItem(AuctionItem targetItem) {
        List<AuctionItem> items = loadItems();

        boolean itemRemoved = false;

        List<AuctionItem> updatedItems = new ArrayList<>();
        for (AuctionItem item : items) {
            if (!itemRemoved
                    && item.sellerId().equals(targetItem.sellerId())
                    && item.price() == targetItem.price()
                    && item.expiryTime().isEqual(targetItem.expiryTime())
                    && item.itemStack().equals(targetItem.itemStack())) {
                itemRemoved = true;
            } else {
                updatedItems.add(item);
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(FILE.toPath(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (AuctionItem item : updatedItems) {
                String serializedItemStack = ItemSerializationUtil.serializeItemStack(item.itemStack());
                String line = item.sellerId() + "," + item.price() + "," + item.expiryTime().format(DATE_FORMATTER) + "," + serializedItemStack;
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static List<AuctionItem> loadItems() {
        List<AuctionItem> items = new ArrayList<>();

        if (!FILE.exists()) {
            try {
                FILE.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return items;
            }
        }

        try {
            List<String> lines = Files.readAllLines(FILE.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    UUID sellerUuid = UUID.fromString(parts[0]);
                    Long price = Long.parseLong(parts[1]);
                    LocalDateTime expirationTime = LocalDateTime.parse(parts[2], DATE_FORMATTER);
                    ItemStack itemStack = ItemSerializationUtil.deserializeItemStack(parts[3]);

                    AuctionItem item = new AuctionItem(sellerUuid, itemStack, price, expirationTime);
                    items.add(item);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return items;
    }

    public static int countItemsByPlayer(UUID sellerUuid) {
        List<AuctionItem> items = loadItems();

        int count = 0;
        for (AuctionItem item : items) {
            if (item.sellerId().equals(sellerUuid)) {
                count++;
            }
        }
        return count;
    }

    public static List<AuctionItem> getItemsByPlayer(UUID sellerUuid) {
        List<AuctionItem> items = loadItems();

        List<AuctionItem> playerItems = new ArrayList<>();
        for (AuctionItem item : items) {
            if (item.sellerId().equals(sellerUuid)) {
                playerItems.add(item);
            }
        }
        return playerItems;
    }
}