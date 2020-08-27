package net.Indyuce.mmocore.command.rpg.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.mmogroup.mmolib.command.api.CommandTreeNode;
import net.mmogroup.mmolib.command.api.Parameter;

public class NoCooldownCommandTreeNode extends CommandTreeNode {
	public NoCooldownCommandTreeNode(CommandTreeNode parent) {
		super(parent, "nocd");

		addParameter(Parameter.PLAYER);
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (args.length < 3)
			return CommandResult.THROW_USAGE;

		Player player = Bukkit.getPlayer(args[2]);
		if (player == null) {
			sender.sendMessage(ChatColor.RED + "Could not find the player called " + args[2] + ".");
			return CommandResult.FAILURE;
		}

		PlayerData data = PlayerData.get(player);
		data.nocd = !data.nocd;
		sender.sendMessage(ChatColor.YELLOW + "NoCD " + (data.nocd ? "enabled" : "disabled") + " for " + player.getName() + ".");
		return CommandResult.SUCCESS;
	}
}