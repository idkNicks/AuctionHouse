package net.starly.auctionhouse.command;

import net.starly.auctionhouse.AuctionHouse;
import net.starly.auctionhouse.context.MessageContent;
import net.starly.auctionhouse.context.MessageType;
import net.starly.auctionhouse.manager.AuctionHouseInventoryManager;
import net.starly.auctionhouse.storage.AuctionItemStorage;
import net.starly.auctionhouse.util.PrivateItemUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
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

        MessageContent content = MessageContent.getInstance();

        if (args.length > 0 && ("리로드".equalsIgnoreCase(args[0]) || "reload".equalsIgnoreCase(args[0]))) {
            if (!sender.isOp()) {
                content.getMessageAfterPrefix(MessageType.ERROR, "notAnOperator").ifPresent(sender::sendMessage);
                return false;
            }

            AuctionHouse.getInstance().reloadConfig();
            content.initialize(AuctionHouse.getInstance().getConfig());
            content.getMessageAfterPrefix(MessageType.NORMAL, "reloadComplete").ifPresent(sender::sendMessage);
            return true;
        }

        if (!(sender instanceof Player)) {
            content.getMessageAfterPrefix(MessageType.ERROR, "noConsoleCommand").ifPresent(sender::sendMessage);
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            AuctionHouseInventoryManager auctionHouseListenerManager = AuctionHouseInventoryManager.getInstance();
            auctionHouseListenerManager.openInventory(player);
            return true;
        }

        if ("판매".equalsIgnoreCase(args[0]) || "sell".equalsIgnoreCase(args[0])) {

            if (args.length == 1) {
                content.getMessageAfterPrefix(MessageType.ERROR, "noPrice").ifPresent(player::sendMessage);
                return false;
            }

            if (args.length == 2) {
                content.getMessageAfterPrefix(MessageType.ERROR, "noAmount").ifPresent(player::sendMessage);
                return false;
            }

            if (args.length != 3) {
                content.getMessageAfterPrefix(MessageType.ERROR, "wrongCommand").ifPresent(player::sendMessage);
                return false;
            }

            int amount;
            long price;

            try {
                amount = Integer.parseInt(args[2]);
                price = Long.parseLong(args[1]);

                if (amount <= 0 || price <= 0) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "negativeValue").ifPresent(sender::sendMessage);
                    player.playSound(player.getLocation(),
                            Sound.valueOf(content.getMessage(MessageType.OTHER, "errorSound.name").orElse("BLOCK_ANVIL_PLACE")),
                            content.getFloat(MessageType.OTHER, "errorSound.volume"),
                            content.getFloat(MessageType.OTHER, "errorSound.pitch"));
                    return true;
                }
            } catch (NumberFormatException exception) {
                content.getMessageAfterPrefix(MessageType.ERROR, "invalidNumber").ifPresent(sender::sendMessage);
                player.playSound(player.getLocation(),
                        Sound.valueOf(content.getMessage(MessageType.OTHER, "errorSound.name").orElse("BLOCK_ANVIL_PLACE")),
                        content.getFloat(MessageType.OTHER, "errorSound.volume"),
                        content.getFloat(MessageType.OTHER, "errorSound.pitch"));
                return true;
            }

            ItemStack itemStack = player.getInventory().getItemInMainHand();

            if (itemStack.getType() == Material.AIR) {
                content.getMessageAfterPrefix(MessageType.ERROR, "noItemInHand").ifPresent(sender::sendMessage);
                player.playSound(player.getLocation(),
                        Sound.valueOf(content.getMessage(MessageType.OTHER, "errorSound.name").orElse("BLOCK_ANVIL_PLACE")),
                        content.getFloat(MessageType.OTHER, "errorSound.volume"),
                        content.getFloat(MessageType.OTHER, "errorSound.pitch"));
                return true;
            }

            if (AuctionItemStorage.countItemsByPlayer(player.getUniqueId()) >= content.getInt(MessageType.OTHER, "countItemsByPlayer")) {
                content.getMessageAfterPrefix(MessageType.ERROR, "limitItem").ifPresent(player::sendMessage);
                return false;
            }

            if (itemStack.getAmount() < amount) {
                content.getMessageAfterPrefix(MessageType.ERROR, "insufficientItem").ifPresent(sender::sendMessage);
                player.playSound(player.getLocation(),
                        Sound.valueOf(content.getMessage(MessageType.OTHER, "errorSound.name").orElse("BLOCK_ANVIL_PLACE")),
                        content.getFloat(MessageType.OTHER, "errorSound.volume"),
                        content.getFloat(MessageType.OTHER, "errorSound.pitch"));
                return true;
            }

            PrivateItemUtil.checkNbtTag(itemStack.clone(), (isBound) -> {
                if (isBound) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "noRegisterPrivateItem").ifPresent(player::sendMessage);
                    player.playSound(player.getLocation(),
                            Sound.valueOf(content.getMessage(MessageType.OTHER, "errorSound.name").orElse("BLOCK_ANVIL_PLACE")),
                            content.getFloat(MessageType.OTHER, "errorSound.volume"),
                            content.getFloat(MessageType.OTHER, "errorSound.pitch"));
                    return;
                }

                LocalDateTime expirationTime = LocalDateTime.now().plus(content.getInt(MessageType.OTHER, "expirationTime"), ChronoUnit.DAYS);
                AuctionItemStorage.storeItem(player.getUniqueId(), price, expirationTime, player.getInventory().getItemInMainHand().clone(), amount);
                itemStack.setAmount(itemStack.getAmount() - amount);
                content.getMessageAfterPrefix(MessageType.NORMAL, "registerAuctionHouse").ifPresent(message -> {
                    String replacedMessage = message.replace("{money}", new DecimalFormat("#,##0").format(price));
                    player.sendMessage(replacedMessage);
                });

                player.playSound(player.getLocation(),
                        Sound.valueOf(content.getMessage(MessageType.OTHER, "completeSound.name").orElse("ENTITY_PLAYER_LEVELUP")),
                        content.getFloat(MessageType.OTHER, "completeSound.volume"),
                        content.getFloat(MessageType.OTHER, "completeSound.pitch"));
            });
            return true;
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
