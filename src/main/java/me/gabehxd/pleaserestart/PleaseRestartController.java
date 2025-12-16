package me.gabehxd.pleaserestart;

import lombok.Getter;
import lombok.Setter;
import me.gabehxd.pleaserestart.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@Setter
@Getter
public class PleaseRestartController implements Listener {
    private boolean needsRestart = false;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PleaseRestart instance = PleaseRestart.getInstance();
        int playerCount = Bukkit.getOnlinePlayers().size() - 1;
        instance.getLogger().info("Player leaving, player count will be " + playerCount);
        if (PleaseRestart.Config.getPlayerThreshold() >= playerCount) {
            if (needsRestart) {
                instance.getLogger().info(PleaseRestart.Config.getPlayerThreshold() + " Players online with pending restart... starting timer.");
                this.scheduleRestart();
            }
        }
    }

    public void attemptRestart() {
        if (PleaseRestart.Config.getPlayerThreshold() >= Bukkit.getOnlinePlayers().size() && needsRestart) {
            PleaseRestart.getInstance().getLogger().info("Restarting server...");
            Bukkit.restart();
        } else {
            PleaseRestart.getInstance().getLogger().info("Players are online, delaying restart...");
        }
    }

    private void scheduleRestart() {
        Bukkit.getScheduler().runTaskLater(PleaseRestart.getInstance(), this::attemptRestart, Utils.timeToTicks(PleaseRestart.Config.getDelay()));
    }
}
