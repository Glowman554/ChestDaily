package de.glowman554.chestdaily;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.glowman554.chestdaily.commands.ChestsCommand;

public class ChestDailyMain extends JavaPlugin
{
	private static ChestDailyMain instance;

	public ChestDailyMain()
	{
		instance = this;
	}

	private FileConfiguration config = getConfig();
	private Location barrelLocation;
	private ChestItem[] chestItems;

	private ChestManager chestManager;

	private void loadBarrelLocation()
	{
		int x = config.getInt("barrel.x");
		int y = config.getInt("barrel.y");
		int z = config.getInt("barrel.z");
		World world = getServer().getWorld(config.getString("barrel.world"));

		barrelLocation = new Location(world, x, y, z);

		getLogger().log(Level.INFO, "Barrel location at " + barrelLocation.toString());

		Block barrel = barrelLocation.getBlock();
		if (barrel.getType() != Material.BARREL)
		{
			throw new IllegalStateException("Block at location " + barrelLocation.toString() + " is not a Barrel! Block is " + barrel.getType());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadChestItems()
	{
		List<String> chestItemsString = (List<String>) config.getList("items");
		chestItems = new ChestItem[chestItemsString.size()];

		for (int i = 0; i < chestItemsString.size(); i++)
		{
			String[] dropItemSplit = chestItemsString.get(i).split("\\*");

			Material item = Material.getMaterial(dropItemSplit[0].toUpperCase());
			if (item == null)
			{
				throw new IllegalArgumentException("Item " + dropItemSplit[0] + " could not be found!");
			}

			chestItems[i] = new ChestItem(item, Integer.parseInt(dropItemSplit[1]));

			getLogger().log(Level.INFO, "Loaded chest item " + chestItems[i].toString());
		}
	}

	@Override
	public void onLoad()
	{
		config.addDefault("barrel.x", 0);
		config.addDefault("barrel.y", 0);
		config.addDefault("barrel.z", 0);
		config.addDefault("barrel.world", "world");

		config.addDefault("items", new String[] {"carrot*16", "apple*32", "dirt*64", "diamond*2"});

		config.options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public void onEnable()
	{
		loadChestItems();
		loadBarrelLocation();

		chestManager = new ChestManager();

		ChestsCommand chests = new ChestsCommand();
		getCommand("kiste").setExecutor(chests);
		getCommand("kiste").setTabCompleter(chests);
		
		getServer().getPluginManager().registerEvents(chestManager, this);
	}

	@Override
	public void onDisable()
	{
	}

	public static ChestDailyMain getInstance()
	{
		return instance;
	}

	public ChestManager getChestManager()
	{
		return chestManager;
	}
	
	public Location getBarrelLocation()
	{
		return barrelLocation;
	}
	
	public ChestItem[] getChestItems()
	{
		return chestItems;
	}
}
