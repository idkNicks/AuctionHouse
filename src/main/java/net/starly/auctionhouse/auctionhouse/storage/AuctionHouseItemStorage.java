package net.starly.auctionhouse.auctionhouse.storage;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.auctionhouse.entity.AuctionItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class AuctionHouseItemStorage {

    private static final File FILE = new File(AuctionHouse.getInstance().getDataFolder(), "auctionhouse.db");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void storeItem(UUID sellerUuid, long price, LocalDateTime expirationTime, ItemStack itemStack) {
        String serializedItemStack = serializeItemStack(itemStack);
        String line = sellerUuid + "," + price + "," + expirationTime.format(DATE_FORMATTER) + "," + serializedItemStack;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static List<AuctionItem> loadItems() {
        List<AuctionItem> items = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(FILE.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    UUID sellerUuid = UUID.fromString(parts[0]);
                    Long price = Long.parseLong(parts[1]);
                    LocalDateTime expirationTime = LocalDateTime.parse(parts[2], DATE_FORMATTER);
                    ItemStack itemStack = deserializeItemStack(parts[3]);

                    AuctionItem item = new AuctionItem(sellerUuid, itemStack, price, expirationTime);
                    items.add(item);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return items;
    }

    private static String serializeItemStack(ItemStack itemStack) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeObject(itemStack);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ItemStack deserializeItemStack(String serializedItemStack) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(serializedItemStack));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            return (ItemStack) dataInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
