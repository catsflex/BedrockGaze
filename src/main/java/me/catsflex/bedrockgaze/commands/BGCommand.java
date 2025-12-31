package me.catsflex.bedrockgaze.commands;

import me.catsflex.bedrockgaze.BedrockGaze;
import me.catsflex.bedrockgaze.runnable.GazeTask;
import me.catsflex.bedrockgaze.utils.Messages;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BGCommand implements CommandExecutor, TabCompleter {
	
	private final GazeTask _task;
	private final BedrockGaze _plugin;
	
	// First arguments
	private static final String _ARGUMENT_1_ON = "on";
	private static final String _ARGUMENT_1_OFF = "off";
	private static final String _ARGUMENT_1_RELOAD = "reload";
	
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
			case _ARGUMENT_1_ON -> {
				
				// Check permission
				if (!sender.hasPermission("bedrockgaze.on")) {
					Messages.ERR_NO_PERM.inform(sender, ChatMessageType.ACTION_BAR);
					return true;
				}
				
				// Notify sender whether the plugin has already been ON or not
				if (_task.isEnabled()) {
					Messages.ERR_ALREADY_ON.inform(sender, ChatMessageType.ACTION_BAR);
				} else {
					_task.setEnabled(true);
					Messages.SUCCESS_CHALLENGE_ON.informAll(ChatMessageType.ACTION_BAR);
				}
			}
			case _ARGUMENT_1_OFF -> {
				
				// Check permission
				if (!sender.hasPermission("bedrockgaze.off")) {
					Messages.ERR_NO_PERM.inform(sender, ChatMessageType.ACTION_BAR);
					return true;
				}
				
				// Notify sender whether the plugin has already been OFF or not
				if (!_task.isEnabled()) {
					Messages.ERR_ALREADY_OFF.inform(sender, ChatMessageType.ACTION_BAR);
				} else {
					_task.setEnabled(false);
					Messages.SUCCESS_CHALLENGE_OFF.informAll(ChatMessageType.ACTION_BAR);
				}
			}
			case _ARGUMENT_1_RELOAD -> {
				
				// Check permission
				if (!sender.hasPermission("bedrockgaze.reload")) {
					Messages.ERR_NO_PERM.inform(sender, ChatMessageType.ACTION_BAR);
					return true;
				}
				
				// Reload logic
				_plugin.reloadConfig();
				_task.loadConfigData();
				Messages.SUCCESS_RELOAD.inform(sender, ChatMessageType.ACTION_BAR);
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
			List<String> allowedArgs = new ArrayList<>();
			
			// Check every permission
			if (sender.hasPermission("bedrockgaze.on")) allowedArgs.add(_ARGUMENT_1_ON);
			if (sender.hasPermission("bedrockgaze.off")) allowedArgs.add(_ARGUMENT_1_OFF);
			if (sender.hasPermission("bedrockgaze.reload")) allowedArgs.add(_ARGUMENT_1_RELOAD);
			
			// Show filtered & sorted suggestions
			var completions = new ArrayList<String>();
			StringUtil.copyPartialMatches(args[0], allowedArgs, completions);
			Collections.sort(completions);
			return completions;
		}
		
		return List.of();
	}
}
