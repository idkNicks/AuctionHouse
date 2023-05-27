package net.starly.auctionhouse;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.starly.auctionhouse.command.AuctionHouseExecutor;
import net.starly.auctionhouse.context.MessageContent;
import net.starly.auctionhouse.listener.AuctionItemExpiryListener;
import net.starly.auctionhouse.scheduler.AuctionItemExpiryScheduler;
import net.starly.auctionhouse.storage.AuctionItemStorage;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class AuctionHouse extends JavaPlugin {

    @Getter private static AuctionHouse instance;
    @Getter private static Economy economy;

    @Override
    public void onLoad() { this.instance = this; }

    @Override
    public void onEnable() {
        if (!isPluginEnable("ST-Core")) {
            getServer().getLogger().warning("[" + getName() + "] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            getServer().getLogger().warning("[" + getName() + "] 다운로드 링크 : http://starly.kr/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!setupEconomy()) {
            getServer().getLogger().warning("[" + getName() + "] Vault 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            getServer().getLogger().warning("[" + getName() + "] 다운로드 링크 : https://www.spigotmc.org/resources/vault.34315/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // CONFIG
        saveDefaultConfig();
        MessageContent.getInstance().initialize(getConfig());

        // SCHEDULER
        AuctionItemStorage auctionItemStorage = new AuctionItemStorage();
        new AuctionItemExpiryScheduler(auctionItemStorage).runTaskTimer(this, 0L, 20L);

        // COMMAND
        AuctionHouseExecutor auctionHouseExecutor = new AuctionHouseExecutor();
        PluginCommand auctionHouse = getServer().getPluginCommand("거래소");

        if (auctionHouse != null) {
            auctionHouse.setExecutor(auctionHouseExecutor);
            auctionHouse.setTabCompleter(auctionHouseExecutor);
        }

        // LISTENER
        getServer().getPluginManager().registerEvents(new AuctionItemExpiryListener(), this);
    }

    private boolean isPluginEnable(String pluginName) {
        Plugin plugin = getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        economy = rsp.getProvider();
        return economy != null;
    }
}
