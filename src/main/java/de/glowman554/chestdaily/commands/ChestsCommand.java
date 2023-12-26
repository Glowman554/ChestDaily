package de.glowman554.chestdaily.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import de.glowman554.chestdaily.ChestDailyMain;

public class ChestsCommand implements CommandExecutor, TabCompleter
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{

		if (args.length == 1 && args[0].equals("reload"))
		{
			ChestDailyMain.getInstance().getChestManager().forceReset();
		}
		else
		{
			sender.sendMessage("Invalid usage!");
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 1)
		{
			return Arrays.asList(new String[] {"reload"});
		}
		return new ArrayList<String>();
	}
}
