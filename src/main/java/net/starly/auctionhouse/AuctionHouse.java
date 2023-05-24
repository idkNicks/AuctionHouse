package net.starly.auctionhouse;

import lombok.Getter;
import net.starly.auctionhouse.command.AuctionHouseExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * AuctionHouse 클래스는 플러그인의 주 클래스입니다.
 * 여기서 플러그인을 초기화하고, 명령어를 등록하며, 이벤트 리스너를 등록합니다.
 *
 * @since 2023-05-21
 * @author idkNicks
 */
public class AuctionHouse extends JavaPlugin {

    @Getter private static AuctionHouse instance;

    /**
     * 플러그인이 로드되었을 때 호출됩니다.
     * 현재 인스턴스를 정적 변수에 저장합니다.
     */
    @Override
    public void onLoad() { this.instance = this; }

    /**
     * 플러그인이 활성화되었을 때 호출됩니다.
     * 필요한 의존성이 있는지 확인하고, 설정 파일을 로드하며, 명령어와 이벤트 리스너를 등록합니다.
     */
    @Override
    public void onEnable() {
        // 필요한 플러그인이 존재하고 활성화되어 있는지 확인합니다.
        if (!isPluginEnable("ST-Core")) {
            // 필요한 플러그인이 존재하지 않으면 경고를 출력하고 플러그인을 비활성화합니다.
            getServer().getLogger().warning("[" + getName() + "] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            getServer().getLogger().warning("[" + getName() + "] 다운로드 링크 : http://starly.kr/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 설정 파일을 로드합니다.
        saveDefaultConfig();

        // 명령어를 등록합니다.
        AuctionHouseExecutor auctionHouseExecutor = new AuctionHouseExecutor();
        PluginCommand auctionHouse = getServer().getPluginCommand("거래소");

        if (auctionHouse != null) {
            auctionHouse.setExecutor(auctionHouseExecutor);
            auctionHouse.setTabCompleter(auctionHouseExecutor);
        }

        // 이벤트 리스너를 등록합니다.
        registerListeners(
        );
    }


    /**
     * 이벤트 리스너를 등록하는 메소드입니다.
     *
     * @param listeners 등록할 이벤트 리스너들
     */
    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    /**
     * 특정 플러그인이 활성화되어 있는지 확인하는 메소드입니다.
     *
     * @param pluginName 확인할 플러그인의 이름
     * @return 플러그인이 활성화되어 있다면 true, 그렇지 않다면 false
     */
    private boolean isPluginEnable(String pluginName) {
        Plugin plugin = getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }
}
