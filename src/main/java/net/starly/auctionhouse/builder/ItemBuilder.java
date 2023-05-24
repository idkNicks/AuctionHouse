package net.starly.auctionhouse.builder;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ItemBuilder 클래스는 아이템스택을 만드는데 사용하는 빌더 클래스입니다.
 *
 * @author idkNicks
 * @since 2023-05-24
 */
public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    /**
     * ItemStack을 이용해 ItemBuilder를 생성합니다.
     *
     * @param item ItemStack 인스턴스
     */
    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    /**
     * Material을 이용해 ItemBuilder를 생성합니다.
     *
     * @param material 아이템의 종류
     */
    public ItemBuilder(Material material) {
        this(material, 1);
    }

    /**
     * Material과 수량을 이용해 ItemBuilder를 생성합니다.
     *
     * @param material 아이템의 종류
     * @param amount   아이템의 수량
     */
    public ItemBuilder(Material material, int amount) {
        item = new ItemStack(material, amount);
        meta = item.getItemMeta();
    }

    /**
     * 아이템의 이름을 설정합니다.
     *
     * @param name 아이템의 이름
     * @return this
     * @throws IllegalArgumentException 만약 이름(name)이 null일 경우
     */
    public ItemBuilder setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("이름(name)은 null이 될 수 없습니다.");
        }
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    /**
     * 아이템의 설명(lore)을 설정합니다.
     *
     * @param lore 아이템의 설명
     * @return this
     */
    public ItemBuilder setLore(String... lore) {
        List<String> coloredLore = Arrays.stream(lore)
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        meta.setLore(coloredLore);
        return this;
    }

    /**
     * 아이템의 설명(lore)을 설정합니다.
     *
     * @param lore 아이템의 설명
     * @return this
     */
    public ItemBuilder setLore(List<String> lore) {
        List<String> coloredLore = lore.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        meta.setLore(coloredLore);
        return this;
    }

    /**
     * 아이템의 파괴 불가능 여부를 설정합니다.
     *
     * @param unbreakable 아이템의 파괴 불가능 여부
     * @return this
     */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    /**
     * 아이템에 마법 부여를 추가합니다.
     *
     * @param enchantment 마법 부여 유형
     * @param level       마법 부여 레벨
     * @return this
     * @throws IllegalArgumentException 만약 마법 부여(enchantment)가 null이거나 레벨(level)이 1 미만일 경우
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        if (enchantment == null) {
            throw new IllegalArgumentException("마법 부여(enchantment)는 null이 될 수 없습니다.");
        }
        if (level < 1) {
            throw new IllegalArgumentException("마법 부여 레벨(level)은 1보다 작을 수 없습니다.");
        }
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * 아이템의 속성을 숨깁니다.
     *
     * @return this
     */
    public ItemBuilder hideAttributes() {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return this;
    }

    /**
     * 스컬 아이템의 소유자를 설정합니다.
     *
     * @param owner 스컬 아이템의 소유자 UUID
     * @return this
     * @throws IllegalArgumentException 만약 소유자(owner)가 null이거나 meta가 SkullMeta의 인스턴스가 아닐 경우
     */
    public ItemBuilder setOwner(UUID owner) {
        if (owner == null) {
            throw new IllegalArgumentException("소유자 UUID(owner)는 null이 될 수 없습니다.");
        }
        if (!(meta instanceof SkullMeta)) {
            throw new IllegalArgumentException("ItemMeta는 SkullMeta의 인스턴스여야 합니다.");
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
        ((SkullMeta) meta).setOwningPlayer(player);
        return this;
    }

    /**
     * 가죽 방어구 아이템의 색을 설정합니다.
     *
     * @param color 가죽 방어구 아이템의 색
     * @return this
     */
    public ItemBuilder setColor(Color color) {
        if (meta instanceof LeatherArmorMeta) ((LeatherArmorMeta) meta).setColor(color);
        return this;
    }

    /**
     * 최종적으로 아이템스택을 생성합니다.
     *
     * @return 생성된 아이템스택
     */
    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
