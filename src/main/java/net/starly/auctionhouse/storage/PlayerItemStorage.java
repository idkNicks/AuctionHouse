package net.starly.auctionhouse.storage;

import lombok.Getter;
import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.entity.impl.WarehouseItem;
import net.starly.auctionhouse.util.ItemSerializationUtil;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class PlayerItemStorage {

    @Getter private static final File PLAYER_ITEMS_FILE = new File(AuctionHouse.getInstance().getDataFolder(), "playeritems.db");

    public static void storeItem(UUID playerId, WarehouseItem item) {
        String serializedItemStack = ItemSerializationUtil.serializeItemStack(item.itemStack());
        String line = playerId + "," + serializedItemStack;

        if (!PLAYER_ITEMS_FILE.exists()) {
            try {
                PLAYER_ITEMS_FILE.createNewFile();
            } catch (IOException e) { e.printStackTrace(); }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(PLAYER_ITEMS_FILE.toPath(), StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void removeItem(UUID playerId, WarehouseItem item) {
        try {
            String itemString = ItemSerializationUtil.serializeItemStack(item.itemStack());
            List<String> lines = Files.readAllLines(PLAYER_ITEMS_FILE.toPath(), StandardCharsets.UTF_8);
            List<String> updatedLines = new ArrayList<>();
            boolean itemRemoved = false;
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    UUID savedPlayerId = UUID.fromString(parts[0]);
                    if (!itemRemoved && savedPlayerId.equals(playerId) && parts[1].equals(itemString)) {
                        itemRemoved = true;
                        continue;
                    }
                }
                updatedLines.add(line);
            }
            Files.write(PLAYER_ITEMS_FILE.toPath(), updatedLines, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static List<WarehouseItem> loadExpiredItem(UUID playerId) {
        List<WarehouseItem> items = new ArrayList<>();

        if (!PLAYER_ITEMS_FILE.exists()) {
            try {
                PLAYER_ITEMS_FILE.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return items;
            }
        }

        try {
            List<String> lines = Files.readAllLines(PLAYER_ITEMS_FILE.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    UUID savedPlayerId;
                    try {
                        savedPlayerId = UUID.fromString(parts[0]);
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                        continue;
                    }
                    if (savedPlayerId.equals(playerId)) {
                        try {
                            ItemStack itemStack = ItemSerializationUtil.deserializeItemStack(parts[1]);
                            items.add(new WarehouseItem(itemStack));
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return items;
    }
}
