package net.starly.auctionhouse.storage;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.util.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class PlayerItemStorage {

    private static final File PLAYER_ITEMS_FILE = new File(AuctionHouse.getInstance().getDataFolder(), "playeritems.db");

    public static void storeItem(UUID playerId, ItemStack item) {
        String serializedItemStack = ItemStackUtil.serializeItemStack(item);
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

    public static List<ItemStack> loadExpiredItem(UUID playerId) {
        List<ItemStack> items = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(PLAYER_ITEMS_FILE.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    UUID savedPlayerId = UUID.fromString(parts[0]);
                    if (savedPlayerId.equals(playerId)) {
                        ItemStack itemStack = ItemStackUtil.deserializeItemStack(parts[1]);
                        items.add(itemStack);
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return items;
    }
}
