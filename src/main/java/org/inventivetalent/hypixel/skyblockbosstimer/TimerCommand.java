package org.inventivetalent.hypixel.skyblockbosstimer;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class TimerCommand extends CommandBase implements ICommand {

	private BossTimerMod mod;

	public TimerCommand(BossTimerMod mod) {
		this.mod = mod;
	}

	@Override
	public String getCommandName() {
		return "bosstimer";
	}

	@Override
	public String getCommandUsage(ICommandSender iCommandSender) {
		return "/BossTimer";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			sender.addChatMessage(new ChatComponentText("§cThe Magma Boss should spawn " + mod.spawnEstimateRelative));
			sender.addChatMessage(new ChatComponentText("§bhttps://hypixel.inventivetalent.org/skyblock-magma-timer/"));
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
}
