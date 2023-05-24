package net.starly.auctionhouse.builder;

import net.starly.core.util.PreCondition;
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

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        item = new ItemStack(material, amount);
        meta = item.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("이름(name)은 null이 될 수 없습니다.");
        }
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        List<String> coloredLore = Arrays.stream(lore)
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        meta.setLore(coloredLore);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        List<String> coloredLore = lore.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        meta.setLore(coloredLore);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

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

    public ItemBuilder hideAttributes() {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return this;
    }

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

    public ItemBuilder setColor(Color color) {
        if (meta instanceof LeatherArmorMeta) ((LeatherArmorMeta) meta).setColor(color);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
