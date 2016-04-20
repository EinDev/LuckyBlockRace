package info.mymc.lbr.util;

import java.sql.SQLException;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import info.mymc.lbr.LuckyBlockRace;
import info.mymc.lbr.LuckyBlockRace.GameState;

public class Tasks {
	
	public static int lobby_task;
	public static boolean lobby_task_running;
	public static int lobby_current;
	private static ArrayList<Integer> lobby_times = new ArrayList<Integer>();
	public static final int LOBBY_MAX = 2 * 60;
	public static final int LOBBY_PLAYERS_MIN = 2;
	public static final String HOST = "localhost";
	public static final String DATABASE = "mini";
	public static final String USER = "root";
	public static final String PASSWORD = "R#199dsx";
	public static SQLWrapper mysql;
	
	public static BossBar boss_bar;
	
	public static void setupTimes() {
		//lobby_times
		lobby_times.add(60);
		lobby_times.add(45);
		lobby_times.add(15);
		lobby_times.add(10);
		lobby_times.add(5);
		lobby_times.add(4);
		lobby_times.add(3);
		lobby_times.add(2);
		lobby_times.add(1);
		
		lobby_current = LOBBY_MAX;
	}
	
	public static void setupBossBar() {
		boss_bar = Bukkit.createBossBar("Das Spiel startet in " + lobby_current + " Sekunden", BarColor.RED, BarStyle.SOLID);
		Bukkit.getOnlinePlayers().stream().forEach((p) -> {
			boss_bar.addPlayer(p);
		});
	}
	
	public static void setupMySQL() throws SQLException {
		mysql = new SQLWrapper(HOST, DATABASE, USER, PASSWORD);
	}
	
	public static void lobby() {
		if(LOBBY_PLAYERS_MIN > Bukkit.getOnlinePlayers().size()) {
			Bukkit.getOnlinePlayers().stream().forEach((p) -> {
				p.playSound(p.getLocation(), Sound.ENTITY_TNT_PRIMED, 1F, 1F);
			});
			Bukkit.broadcastMessage(LuckyBlockRace.getInstance().prefix + "Der Countdown wurde gestoppt");
			Bukkit.getScheduler().cancelTask(lobby_task);
			lobby_task = -1;
			boss_bar.removeAll();
			boss_bar = null;
			lobby_current = LOBBY_MAX;
		}
		if(lobby_times.contains(lobby_current)) {
			Bukkit.getOnlinePlayers().stream().forEach((p) -> {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1F, 1F);
			});
			if(lobby_current == 1) {
				Bukkit.broadcastMessage(LuckyBlockRace.getInstance().prefix + "Das Spiel startet in " + lobby_current + " Sekunde");
			} else {
				Bukkit.broadcastMessage(LuckyBlockRace.getInstance().prefix + "Das Spiel startet in " + lobby_current + " Sekunden");
			}
		}
		switch(lobby_current) {
		case 0:
			Bukkit.getOnlinePlayers().stream().forEach((p) -> {
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
			});
			Bukkit.broadcastMessage(LuckyBlockRace.getInstance().prefix + "Das Spiel startet nun");
			Bukkit.getScheduler().cancelTask(lobby_task);
			lobby_task = -1;
			boss_bar.removeAll();
			boss_bar = null;
			Bukkit.getScheduler().runTaskAsynchronously(LuckyBlockRace.getInstance(), Tasks::prepareGame);
			break;
		}
		if(true) {
			if(boss_bar != null) {
				boss_bar.setProgress((double) lobby_current / (double) LOBBY_MAX);
				System.out.println((double) lobby_current / (double) LOBBY_MAX);
				boss_bar.setTitle("Das Spiel startet in " + lobby_current + " Sekunden");
			}
		}
		lobby_current--;
	}
	
	public static void addToBossBar(Player p) {
		boss_bar.addPlayer(p);
	}
	
	public static void prepareGame() {
		Bukkit.getOnlinePlayers().stream().forEach((p) -> {
			p.teleport(p.getWorld().getSpawnLocation());
		});
		LuckyBlockRace.getInstance().state = GameState.INGAME;
	}
	
	public static void shutdownBossBar() {
		boss_bar.removeAll();
	}
	
	public static void shutdownMySQL() throws SQLException {
		mysql.close();
	}

}
