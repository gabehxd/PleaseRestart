package me.gabehxd.pleaserestart;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class PleaseRestart extends JavaPlugin {
    @Getter
    private static PleaseRestart instance;
    @Getter
    private PleaseRestartController restartController;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        restartController = new PleaseRestartController();
        getServer().getPluginManager().registerEvents(restartController, this);

        new PleaseRestartCommand().registerCommand(getCommand("pleaserestart"));
    }

    public static class Config {
        public static int getPlayerThreshold() {
            return PleaseRestart.getInstance().getConfig().getInt("playerCount", 0);
        }

        public static int getDelay() {
            return PleaseRestart.getInstance().getConfig().getInt("restartDelay", 10);
        }
    }
}
