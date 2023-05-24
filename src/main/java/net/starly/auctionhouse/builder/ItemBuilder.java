package net.starly.auctionhouse.builder;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * ItemBuilder 클래스는 아이템스택을 만드는데 사용하는 빌더 클래스입니다.
 *
 * @since 2023-05-24
 * @author idkNicks
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
     * @param amount 아이템의 수량
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
     */
    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    /**
     * 아이템의 설명(lore)을 설정합니다.
     *
     * @param lore 아이템의 설명
     * @return this
     */
    public ItemBuilder setLore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    /**
     * 아이템의 설명(lore)을 설정합니다.
     *
     * @param lore 아이템의 설명
     * @return this
     */
    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(lore);
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
     * @param level 마법 부여 레벨
     * @return this
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
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
     */
    public ItemBuilder setOwner(UUID owner) {
        if (meta instanceof SkullMeta) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
            ((SkullMeta) meta).setOwningPlayer(player);
        }
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
