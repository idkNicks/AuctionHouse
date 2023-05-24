package net.starly.auctionhouse.command;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.entity.impl.AuctionItem;
import net.starly.auctionhouse.inventory.AuctionHouseInventory;
import net.starly.auctionhouse.manager.AuctionHouseListenerManager;
import net.starly.auctionhouse.page.AuctionHousePageHolder;
import net.starly.auctionhouse.storage.AuctionItemStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AuctionHouseExecutor implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        if (args.length == 0) {
            AuctionHouseListenerManager.openAuctionHouse(player);
            return true;
        }

        switch (args[0]) {

            case "판매", "sell" -> {
                final long price;

                try {
                    price = Long.parseLong(args[1]);
                } catch (NumberFormatException exception) {
                    player.sendMessage("숫자만 입력해야 합니다.");
                    return true;
                }

                final int amount = Integer.parseInt(args[2]);

                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if (itemStack.getType() == Material.AIR) {
                    player.sendMessage("물건을 들고 있어야 합니다.");
                    return true;
                }

//                LocalDateTime expirationTime = LocalDateTime.now().plus(7, ChronoUnit.HOURS);
                LocalDateTime expirationTime = LocalDateTime.now().plus(5, ChronoUnit.SECONDS);


                AuctionItemStorage.storeItem(player.getUniqueId(), price, expirationTime, player.getInventory().getItemInMainHand().clone(), amount);
                itemStack.setAmount(itemStack.getAmount() - amount);
                player.sendMessage("경매 아이템이 등록되었습니다.");
                return true;
            }

            case "리로드", "reload" -> {
                AuctionHouse.getInstance().reloadConfig();
                player.sendMessage("콘피그를 리로드 하였습니다.");
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        final Player player = (Player) sender;
        final List<String> tabList = new ArrayList<>();

        if (args.length == 1) {
            tabList.add("판매");
            if (player.isOp()) tabList.add("리로드");
            return StringUtil.copyPartialMatches(args[0], tabList, new ArrayList<>());
        }

        if (args.length == 2 && "판매".equals(args[0]) || "sell".equals(args[0])) {
            final List<String> numberList = IntStream.rangeClosed(1, 10)
                    .map(i -> i * 10000)
                    .mapToObj(Integer::toString)
                    .collect(Collectors.toList());
            return StringUtil.copyPartialMatches(args[1], numberList, new ArrayList<>());
        }

        if (args.length == 3 && "판매".equals(args[0]) || "sell".equals(args[0])) {
            final List<String> numberList = IntStream.rangeClosed(1, 10)
                    .mapToObj(Integer::toString)
                    .collect(Collectors.toList());
            return StringUtil.copyPartialMatches(args[2], numberList, new ArrayList<>());
        }

        return Collections.emptyList();
    }
}
