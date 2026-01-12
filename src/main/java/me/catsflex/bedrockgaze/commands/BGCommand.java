package me.catsflex.bedrockgaze.commands;

import me.catsflex.bedrockgaze.BedrockGaze;
import me.catsflex.bedrockgaze.config.PluginConfiguration;
import me.catsflex.bedrockgaze.runnable.GazeTask;
import me.catsflex.bedrockgaze.utils.Messages;
import me.catsflex.bedrockgaze.utils.Permissions;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BGCommand implements CommandExecutor, TabCompleter {
	
	private final GazeTask _task;
	private final BedrockGaze _plugin;
	
	// First arguments
	private static final String _ARG_1_ON = "on";
	private static final String _ARG_1_OFF = "off";
	private static final String _ARG_1_RELOAD = "reload";
	
	public BGCommand(GazeTask task, BedrockGaze plugin) {
		_task = task;
		_plugin = plugin;
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		
		// Check for args
		if (args.length == 0) {
			Messages.ERR_INSUFFICIENT_ARGS.inform(sender, ChatMessageType.ACTION_BAR);
			return true;
		}
		
		var first = args[0].toLowerCase();
		
		// First argument
		switch (first) {
			case _ARG_1_ON -> {
				
				// Check permission
				if (!sender.hasPermission(Permissions.ON.toString())) {
					Messages.ERR_NO_PERM.inform(sender, ChatMessageType.ACTION_BAR);
					return true;
				}
				
				// Notify sender whether the plugin has already been ON or not
				if (_task.isEnabled()) {
					Messages.WARN_ALREADY_ON.inform(sender, ChatMessageType.ACTION_BAR);
				} else {
					_task.setEnabled(true);
					Messages.SUCCESS_CHALLENGE_ON.informAll(ChatMessageType.ACTION_BAR);
				}
			}
			case _ARG_1_OFF -> {
				
				// Check permission
				if (!sender.hasPermission(Permissions.OFF.toString())) {
					Messages.ERR_NO_PERM.inform(sender, ChatMessageType.ACTION_BAR);
					return true;
				}
				
				// Notify sender whether the plugin has already been OFF or not
				if (!_task.isEnabled()) {
					Messages.WARN_ALREADY_OFF.inform(sender, ChatMessageType.ACTION_BAR);
				} else {
					_task.setEnabled(false);
					Messages.SUCCESS_CHALLENGE_OFF.informAll(ChatMessageType.ACTION_BAR);
				}
			}
			case _ARG_1_RELOAD -> {
				
				// Check permission
				if (!sender.hasPermission(Permissions.RELOAD.toString())) {
					Messages.ERR_NO_PERM.inform(sender, ChatMessageType.ACTION_BAR);
					return true;
				}
				
				// Notify sender whether the config reload has been successful or not
				boolean isSuccessful = PluginConfiguration.load(_plugin);
				if (!isSuccessful) {
					Messages.WARN_RELOAD.inform(sender, ChatMessageType.ACTION_BAR);
				} else {
					Messages.SUCCESS_RELOAD.inform(sender, ChatMessageType.ACTION_BAR);
				}
			}
			default -> {
				Messages.ERR_UNRESOLVED_CMD.inform(sender, ChatMessageType.ACTION_BAR);
			}
		}
		
		return true;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		
		if (args.length == 1) {
			
			// Check every permission
			List<String> allowedArgs = new ArrayList<>();
			if (sender.hasPermission(Permissions.ON.toString())) allowedArgs.add(_ARG_1_ON);
			if (sender.hasPermission(Permissions.OFF.toString())) allowedArgs.add(_ARG_1_OFF);
			if (sender.hasPermission(Permissions.RELOAD.toString())) allowedArgs.add(_ARG_1_RELOAD);
			return getPartialMatches(args[0], allowedArgs);
		}
		
		return List.of();
	}
	
	private List<String> getPartialMatches(String input, List<String> all) {
		List<String> suggestions = new ArrayList<>();
		StringUtil.copyPartialMatches(input, all, suggestions);
		return suggestions;
	}
	
	// Unused for now
	private List<String> getBlockSuggestions(String input) {
		
		List<String> blocks = new ArrayList<>();
		for (var m : Material.values()) {
			if (!m.isBlock()) continue;
			
			var namespacedKey = m.getKey().toString();
			if (!namespacedKey.contains(input.toLowerCase())) continue;
			
			blocks.add(namespacedKey);
		}
		
		// Limiting list size, so the client does not lag after receiving hundreds of blocks
		int MAX_SIZE = 64;
		return blocks.size() > MAX_SIZE ? blocks.subList(0, MAX_SIZE) : blocks;
	}
}
