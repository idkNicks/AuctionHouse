package net.starly.auctionhouse.auctionhouse.storage;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.auctionhouse.entity.AuctionItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * AuctionHouseItemStorage 클래스는 경매 아이템을 파일에 저장하고 로드하는 작업을 담당합니다.
 *
 * @since 2023-05-24
 * @author idkNicks
 */
public class AuctionHouseItemStorage {

    private static final File FILE = new File(AuctionHouse.getInstance().getDataFolder(), "auctionhouse.db");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시mm분ss초");

    /**
     * 경매 아이템을 파일에 저장합니다.
     *
     * @param sellerUuid 판매자 UUID
     * @param price 가격
     * @param expirationTime 만료 시간
     * @param itemStack 아이템 스택
     */
    public static void storeItem(UUID sellerUuid, long price, LocalDateTime expirationTime, ItemStack itemStack) {
        // 아이템 스택을 직렬화하고, 각 항목을 CSV 형태의 문자열로 만듭니다.
        String serializedItemStack = serializeItemStack(itemStack);
        String line = sellerUuid + "," + price + "," + expirationTime.format(DATE_FORMATTER) + "," + serializedItemStack;

        // 파일이 없다면 새 파일을 생성합니다.
        if (!FILE.exists()) {
            try {
                FILE.createNewFile();
            } catch (IOException e) { e.printStackTrace(); }
        }

        // 줄을 파일에 추가합니다.
        try (BufferedWriter writer = Files.newBufferedWriter(FILE.toPath(), StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * 파일에서 모든 경매 아이템을 로드합니다.
     *
     * @return 경매 아이템의 리스트
     */
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

    /**
     * ItemStack 객체를 Base64 문자열로 직렬화합니다.
     *
     * @param itemStack 직렬화할 ItemStack 객체
     * @return 직렬화된 문자열
     */
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

    /**
     * Base64 문자열을 ItemStack 객체로 역직렬화합니다.
     *
     * @param serializedItemStack 역직렬화할 Base64 문자열
     * @return 역직렬화된 ItemStack 객체
     */
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
