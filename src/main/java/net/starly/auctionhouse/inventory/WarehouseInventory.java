package net.starly.auctionhouse.inventory;


import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

@AllArgsConstructor
public class WarehouseInventory {

    private final Inventory inventory;

    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        player.sendMessage("클릭했네");
    }
}
