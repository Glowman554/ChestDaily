package de.glowman554.chestdaily.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.glowman554.chestdaily.ChestDailyMain;

public class AutoResettingArrayList<E> extends ArrayList<E>
{
	private static final long serialVersionUID = 1L;

	private final File persistanceFile;
	private FileConfiguration persistance;

	private ArrayList<Hook> hooks = new ArrayList<>();

	public AutoResettingArrayList(String persistanceId, Mapper<E> map)
	{
		persistanceFile = new File(ChestDailyMain.getInstance().getDataFolder(), persistanceId + ".yml");
		persistance = YamlConfiguration.loadConfiguration(persistanceFile);

		int day = 20 * 60 * 60 * 24;
		long initial = secondsUntilMidnight() * 20;
		Bukkit.getServer().getScheduler().runTaskTimer(ChestDailyMain.getInstance(), this::onMidnight, initial, day);

		load(map);
	}

	@SuppressWarnings("unchecked")
	private void load(Mapper<E> map)
	{
		ChestDailyMain.getInstance().getLogger().log(Level.INFO, "Loading persistant auto reset from " + persistanceFile.getPath());

		if (persistance.contains("persistance"))
		{
			for (String entry : (List<String>) persistance.getList("persistance"))
			{
				add(map.map(entry));
			}
		}
	}

	public void save()
	{
		persistance.set("persistance", stream().map(v -> v.toString()).toArray());
		try
		{
			persistance.save(persistanceFile);
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public void forceReset()
	{
		onMidnight();
	}

	private long secondsUntilMidnight()
	{
		long current = System.currentTimeMillis();
		long midnight = ((current / 1000 / 60 / 60 / 24) + 1) * 1000 * 60 * 60 * 24;
		return (midnight - current) / 1000;
	}

	private void onMidnight()
	{
		clear();
		save();

		for (Hook hook : hooks)
		{
			hook.exec();
		}
	}

	public void hook(Hook hook)
	{
		hooks.add(hook);
	}

	public interface Mapper<E>
	{
		E map(String string);
	}

	public interface Hook
	{
		void exec();
	}
}
