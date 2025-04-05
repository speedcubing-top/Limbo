package top.speedcubing.limbo;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import top.speedcubing.lib.bukkit.pluginMessage.BungeePluginMessage;
import top.speedcubing.server.utils.MapManager;

public class Limbo extends JavaPlugin implements Listener {

    public void lobby(Player player) {
        BungeePluginMessage.switchServer(player, "lobby");
    }

    public void onEnable() {
        MinecraftServer.getServer().getPropertyManager().setProperty("server-name", "limbo");
        Bukkit.getPluginManager().registerEvents(this, this);

        try {
            MapManager.install("map_limbo", "map_limbo", Difficulty.PEACEFUL, 6000, false);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getServer().shutdown();
        }
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        e.getPlayer().setGameMode(GameMode.SURVIVAL);
        e.getPlayer().teleport(new Location(Bukkit.getWorld("map_limbo"), 0.5, 100, 0.5, 0, 0));
        for (Player p : Bukkit.getOnlinePlayers()) {
            e.getPlayer().hidePlayer(p);
            p.hidePlayer(e.getPlayer());
        }
    }

    @EventHandler
    public void AsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
            lobby(e.getPlayer());
    }

    @EventHandler
    public void PlayerToggleSneakEvent(PlayerToggleSneakEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
            lobby(e.getPlayer());
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
            lobby(e.getPlayer());
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        e.setQuitMessage("");
    }

    @EventHandler
    public void FoodLevelChangeEvent(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent e) {
        e.setCancelled(true);
    }
}
