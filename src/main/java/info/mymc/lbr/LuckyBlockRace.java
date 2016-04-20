package info.mymc.lbr;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import info.mymc.lbr.util.Tasks;
import net.md_5.bungee.api.ChatColor;

public class LuckyBlockRace extends JavaPlugin implements Listener {

	private static LuckyBlockRace instance;

	public final String prefix = "[" + ChatColor.YELLOW + "LuckyBlockRace" + ChatColor.RESET + "] " + ChatColor.YELLOW;

	public GameState state = GameState.LOBBY;

	@SuppressWarnings("static-access")
	@Override
	public void onEnable() {
		this.instance = this;
		saveDefaultConfig();
		Tasks.setupTimes();
		Tasks.setupBossBar();
		try {
			Tasks.setupMySQL();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Tasks.lobby_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Tasks::lobby, 0L, 20L);
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		Tasks.shutdownBossBar();
		try {
			Tasks.shutdownMySQL();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static LuckyBlockRace getInstance() {
		return LuckyBlockRace.instance;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		AttributeInstance instance = e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		if (instance != null) {
			instance.setBaseValue(16.0D);
		}
		e.setJoinMessage(String.format(prefix + "%s has joined the game (%d/%d)", e.getPlayer().getName(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers()));
		e.getPlayer().setGameMode(GameMode.ADVENTURE);
		if(state.equals(GameState.LOBBY)) {
			if(Tasks.lobby_task == -1 && Bukkit.getOnlinePlayers().size() >= Tasks.LOBBY_PLAYERS_MIN) {
				Tasks.setupBossBar();
				Tasks.lobby_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Tasks::lobby, 0L, 20L);
			}
		}

	}

	public enum GameState {
		LOBBY,
		INGAME,
		END
	}

}
