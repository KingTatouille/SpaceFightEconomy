package hillwalk.fr.spacefighteconomy.listener;

import hillwalk.fr.spacefighteconomy.EconomyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeavePlayer implements Listener {

    private final EconomyManager economyManager;

    public LeavePlayer(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        economyManager.saveData();
    }
}
