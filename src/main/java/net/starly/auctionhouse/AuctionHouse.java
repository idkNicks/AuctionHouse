package net.starly.auctionhouse;

import lombok.Getter;
import net.starly.auctionhouse.command.AuctionHouseExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class AuctionHouse extends JavaPlugin {

    @Getter private static AuctionHouse instance;

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

        saveDefaultConfig();

        AuctionHouseExecutor auctionHouseExecutor = new AuctionHouseExecutor();
        PluginCommand auctionHouse = getServer().getPluginCommand("거래소");

        if (auctionHouse != null) {
            auctionHouse.setExecutor(auctionHouseExecutor);
            auctionHouse.setTabCompleter(auctionHouseExecutor);
        }

        registerListeners(
        );
    }


    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private boolean isPluginEnable(String pluginName) {
        Plugin plugin = getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }
}
