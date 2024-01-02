package top.speedcubing.limbo;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import top.speedcubing.lib.bukkit.pluginMessage.BungeePluginMessage;

import java.net.*;
import java.nio.file.*;

public class Limbo extends JavaPlugin implements Listener {
    //MapManager
    public static World install(String url, String map, Difficulty difficulty, int time, boolean autoSave) throws Exception {
        long t = System.currentTimeMillis();
        World world = Bukkit.getWorld(map);
        System.out.println("[MapManager] installing \"" + map + "\"" + (url == null ? "" : (" from \"" + map + "\"")));
        if (url != null) {
            new ProcessBuilder("rm", "-r", map).start().waitFor();
            String file = url + ".tar.gz";

            HttpURLConnection connection = (HttpURLConnection) new URL("https://speedcubing.top/maps/" + file).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.0");
            String key = "2ad5a999-854a-4c5e-adbc-aa434deb79d3";
            connection.setRequestProperty("Authorization", "Bearer " + key);

            Files.copy(connection.getInputStream(), Paths.get(file), StandardCopyOption.REPLACE_EXISTING);
            new ProcessBuilder("tar", "-xvzf", file).start().waitFor();
            new ProcessBuilder("mv", url, map).start().waitFor();
            new ProcessBuilder("rm", file).start().waitFor();
            world = Bukkit.createWorld(new WorldCreator(map));
        }
        world.setDifficulty(difficulty);
        world.setTime(time);
        world.setAutoSave(autoSave);
        t = System.currentTimeMillis() - t;
        System.out.println("[MapManager] \"" + map + "\" installing finished. (" + String.format("%.3fs", t / 1000D) + ")");
        return world;
    }

    public void lobby(Player player) {
        BungeePluginMessage.switchServer(player, "lobby");
    }

    public void onEnable() {
        MinecraftServer.getServer().getPropertyManager().setProperty("server-name", "limbo");
        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getPluginCommand("hub").setExecutor((commandSender, b, c, strings) -> {
            if (strings.length == 0) {
                lobby((Player) commandSender);
            } else commandSender.sendMessage("/hub, /l, /lobby");
            return true;
        });

        try {
            install("map_limbo", "map_limbo", Difficulty.PEACEFUL, 6000, false);
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
