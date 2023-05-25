package net.starly.auctionhouse.storage;

import lombok.Getter;
import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.entity.impl.WarehouseItem;
import net.starly.auctionhouse.util.ItemStackUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


public class PlayerItemStorage {

    @Getter private static final File PLAYER_ITEMS_FILE = new File(AuctionHouse.getInstance().getDataFolder(), "playeritems.db");

    public static void storeItem(UUID playerId, WarehouseItem item) {
        String serializedItemStack = ItemStackUtil.serializeItemStack(item.itemStack());
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
            String itemString = ItemStackUtil.serializeItemStack(item.itemStack());
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
                            WarehouseItem itemStack = ItemStackUtil.deserializeItemStack(parts[1]);
                            items.add(itemStack);
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return items;
    }

    public static String serializeItemStack(WarehouseItem itemStack) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeObject(itemStack);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WarehouseItem deserializeItemStack(String serializedItemStack) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(serializedItemStack));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            return (WarehouseItem) dataInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
