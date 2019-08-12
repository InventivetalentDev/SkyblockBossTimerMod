package org.inventivetalent.hypixel.skyblockbosstimer;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class TimerCommand extends CommandBase implements ICommand {

	private BossTimerMod mod;

	public TimerCommand(BossTimerMod mod) {
		this.mod = mod;
	}

	@Override
	public String getName() {
		return "bosstimer";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/BossTimer";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			sender.sendMessage(new TextComponentString("The Magma Boss should spawn " + mod.spawnEstimateRelative));
			sender.sendMessage(new TextComponentString("https://hypixel.inventivetalent.org/skyblock-magma-timer/"));
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
}
