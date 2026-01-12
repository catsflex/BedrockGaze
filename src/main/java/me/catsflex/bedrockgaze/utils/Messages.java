package me.catsflex.bedrockgaze.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public enum Messages {
	
	// Errors
	ERR_NO_PERM("✘ You do not have permission to use this command!", ChatColor.RED),
	ERR_INSUFFICIENT_ARGS("✘ Insufficient arguments!", ChatColor.RED),
	ERR_UNRESOLVED_CMD("✘ Unresolved command!", ChatColor.RED),
	
	// Warnings
	WARN_RELOAD("⚠ Couldn't fully parse the config, check the console for more info!", ChatColor.YELLOW),
	WARN_ALREADY_ON("⚠ The challenge has already been ON!", ChatColor.YELLOW),
	WARN_ALREADY_OFF("⚠ The challenge has already been OFF!", ChatColor.YELLOW),
	
	// Success
	SUCCESS_RELOAD("✔ The config has been reloaded!", ChatColor.GREEN),
	SUCCESS_CHALLENGE_ON("✔ The challenge is ON!", ChatColor.GREEN),
	SUCCESS_CHALLENGE_OFF("✔ The challenge is OFF!", ChatColor.GREEN);
	
	private final String _text;
	private final ChatColor _color;
	
	Messages(@NotNull String text, @NotNull ChatColor color) {
		_text = text;
		_color = color;
	}
	
	@Override
	public String toString() {
		return _color + _text;
	}
	
	public void informAll(@NotNull ChatMessageType playerMessageType) {
		broadcastToConsole();
		broadcastToAllPlayers(playerMessageType);
	}
	
	public void inform(@NotNull CommandSender sender, @NotNull ChatMessageType playerMessageType) {
		if (sender instanceof Player player) {
			broadcastToConsole();
			player.spigot().sendMessage(playerMessageType, new TextComponent(this.toString()));
		} else {
			sender.sendMessage(this.toString());
		}
	}
	
	private void broadcastToConsole() {
		Bukkit.getConsoleSender().sendMessage(this.toString());
	}
	
	private void broadcastToAllPlayers(@NotNull ChatMessageType playerMessageType) {
		var tc = new TextComponent(this.toString());
		for (var player : Bukkit.getOnlinePlayers()) {
			player.spigot().sendMessage(playerMessageType, tc);
		}
	}
}
