package net.Indyuce.mmocore.command.rpg.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.experience.Profession;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttributes.AttributeInstance;
import net.mmogroup.mmolib.command.api.CommandTreeNode;
import net.mmogroup.mmolib.command.api.Parameter;

public class ResetCommandTreeNode extends CommandTreeNode {
	public ResetCommandTreeNode(CommandTreeNode parent) {
		super(parent, "reset");

		addChild(new ResetLevelsCommandTreeNode(this));
		addChild(new ResetSkillsCommandTreeNode(this));
		addChild(new ResetAllCommandTreeNode(this));
		addChild(new ResetAttributesCommandTreeNode(this));
		addChild(new ResetWaypointsCommandTreeNode(this));
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		return CommandResult.THROW_USAGE;
	}

	public class ResetAllCommandTreeNode extends CommandTreeNode {
		public ResetAllCommandTreeNode(CommandTreeNode parent) {
			super(parent, "all");

			addParameter(Parameter.PLAYER);
		}

		@Override
		public CommandResult execute(CommandSender sender, String[] args) {
			if (args.length < 4)
				return CommandResult.THROW_USAGE;

			Player player = Bukkit.getPlayer(args[3]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Could not find the player called " + args[3] + ".");
				return CommandResult.FAILURE;
			}

			PlayerData data = PlayerData.get(player);
			data.setLevel(1);
			data.setExperience(0);
			for (Profession profession : MMOCore.plugin.professionManager.getAll()) {
				data.getCollectionSkills().setExperience(profession, 0);
				data.getCollectionSkills().setLevel(profession, 0);
			}
			MMOCore.plugin.classManager.getAll().forEach(profess -> data.unloadClassInfo(profess));
			data.setClassPoints(0);
			data.setSkillPoints(0);

			data.setAttributePoints(0);
			data.setAttributeReallocationPoints(0);
			data.getAttributes().getInstances().forEach(ins -> ins.setBase(0));

			data.mapSkillLevels().forEach((skill, level) -> data.resetSkillLevel(skill));
			while (data.hasSkillBound(0))
				data.unbindSkill(0);
			data.getQuestData().resetFinishedQuests();
			data.getQuestData().start(null);
			sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "'s data was succesfully reset.");
			return CommandResult.SUCCESS;
		}
	}

	public class ResetWaypointsCommandTreeNode extends CommandTreeNode {
		public ResetWaypointsCommandTreeNode(CommandTreeNode parent) {
			super(parent, "waypoints");

			addParameter(Parameter.PLAYER);
		}

		@Override
		public CommandResult execute(CommandSender sender, String[] args) {
			if (args.length < 4)
				return CommandResult.THROW_USAGE;

			Player player = Bukkit.getPlayer(args[3]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Could not find the player called " + args[3] + ".");
				return CommandResult.FAILURE;
			}

			PlayerData data = PlayerData.get(player);
			data.getWaypoints().clear();
			MMOCore.plugin.waypointManager.getAll().stream().filter(waypoint -> waypoint.isDefault())
					.forEach(waypoint -> data.unlockWaypoint(waypoint));
			return CommandResult.SUCCESS;
		}
	}

	public class ResetQuestsCommandTreeNode extends CommandTreeNode {
		public ResetQuestsCommandTreeNode(CommandTreeNode parent) {
			super(parent, "quests");

			addParameter(Parameter.PLAYER);
		}

		@Override
		public CommandResult execute(CommandSender sender, String[] args) {
			if (args.length < 4)
				return CommandResult.THROW_USAGE;

			Player player = Bukkit.getPlayer(args[3]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Could not find the player called " + args[3] + ".");
				return CommandResult.FAILURE;
			}

			PlayerData data = PlayerData.get(player);
			data.getQuestData().resetFinishedQuests();
			data.getQuestData().start(null);
			return CommandResult.SUCCESS;
		}
	}

	public class ResetSkillsCommandTreeNode extends CommandTreeNode {
		public ResetSkillsCommandTreeNode(CommandTreeNode parent) {
			super(parent, "skills");

			addParameter(Parameter.PLAYER);
		}

		@Override
		public CommandResult execute(CommandSender sender, String[] args) {
			if (args.length < 4)
				return CommandResult.THROW_USAGE;

			Player player = Bukkit.getPlayer(args[3]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Could not find the player called " + args[3] + ".");
				return CommandResult.FAILURE;
			}

			PlayerData data = PlayerData.get(player);
			data.mapSkillLevels().forEach((skill, level) -> data.resetSkillLevel(skill));
			while (data.hasSkillBound(0))
				data.unbindSkill(0);
			sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "'s skill data was succesfully reset.");
			return CommandResult.SUCCESS;
		}
	}

	public class ResetAttributesCommandTreeNode extends CommandTreeNode {
		public ResetAttributesCommandTreeNode(CommandTreeNode parent) {
			super(parent, "attributes");

			addParameter(Parameter.PLAYER);
			addParameter(new Parameter("(-reallocate)", (explore, list) -> list.add("-reallocate")));
		}

		@Override
		public CommandResult execute(CommandSender sender, String[] args) {
			if (args.length < 4)
				return CommandResult.THROW_USAGE;

			Player player = Bukkit.getPlayer(args[3]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Could not find the player called " + args[3] + ".");
				return CommandResult.FAILURE;
			}

			PlayerData data = PlayerData.get(player);

			/*
			 * force reallocating of player attribute points
			 */
			if (args.length > 4 && args[4].equalsIgnoreCase("-reallocate")) {

				int points = 0;
				for (AttributeInstance ins : data.getAttributes().getInstances()) {
					points += ins.getBase();
					ins.setBase(0);
				}

				data.giveAttributePoints(points);
				sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "'s attribute points spendings were successfully reset.");
				return CommandResult.SUCCESS;
			}

			data.getAttributes().getInstances().forEach(ins -> ins.setBase(0));
			sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "'s attributes were succesfully reset.");
			return CommandResult.SUCCESS;
		}
	}

	public class ResetLevelsCommandTreeNode extends CommandTreeNode {
		public ResetLevelsCommandTreeNode(CommandTreeNode parent) {
			super(parent, "levels");

			addParameter(Parameter.PLAYER);
		}

		@Override
		public CommandResult execute(CommandSender sender, String[] args) {
			if (args.length < 4)
				return CommandResult.THROW_USAGE;

			Player player = Bukkit.getPlayer(args[3]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Could not find the player called " + args[3] + ".");
				return CommandResult.FAILURE;
			}

			PlayerData data = PlayerData.get(player);
			data.setLevel(1);
			data.setExperience(0);
			for (Profession profession : MMOCore.plugin.professionManager.getAll()) {
				data.getCollectionSkills().setExperience(profession, 0);
				data.getCollectionSkills().setLevel(profession, 0);
			}
			sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "'s levels were succesfully reset.");

			return CommandResult.SUCCESS;
		}
	}
}