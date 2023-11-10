package de.glowman554.chestdaily;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestManager implements Listener
{
	private HashMap<String, Inventory> inventories = new HashMap<>();
	private Random random = new Random();

	public void scheduleRefillTask()
	{
		int day = 20 * 60 * 60 * 24;
		long initial = secondsUntilMidnight() * 20;

		Bukkit.getLogger().log(Level.INFO, String.format("Scheduling task in %d ticks every %d ticks", initial, day));

		Bukkit.getServer().getScheduler().runTaskTimer(ChestDailyMain.getInstance(), this::onMidnight, initial, day);
	}

	private long secondsUntilMidnight()
	{
		long current = System.currentTimeMillis();
		long midnight = ((current / 1000 / 60 / 60 / 24) + 1) * 1000 * 60 * 60 * 24;
		return (midnight - current) / 1000;
	}

	private void onMidnight()
	{
		resetChests();
	}

	public void resetChests()
	{
		Bukkit.getLogger().log(Level.INFO, "Resetting chests!");
		inventories.clear();
	}

	private void openInventoryFor(HumanEntity player)
	{
		if (inventories.get(player.getUniqueId().toString()) == null)
		{
			Inventory inventory = Bukkit.createInventory(player, 9 * 3, "§c§lLootkiste");
			inventories.put(player.getUniqueId().toString(), inventory);

			int slotsToFill = random.nextInt(3, 11);
			for (int i = 0; i < slotsToFill; i++)
			{
				int slot = random.nextInt(0, 9 * 3);
				ChestItem item = ChestDailyMain.getInstance().getChestItems()[random.nextInt(0, ChestDailyMain.getInstance().getChestItems().length)];

				inventory.setItem(slot, new ItemStack(item.getItem(), item.getAmmount()));
			}
		}

		player.openInventory(inventories.get(player.getUniqueId().toString()));
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event)
	{
		if (event.getInventory().getHolder() instanceof Barrel)
		{
			Barrel barrel = (Barrel) event.getInventory().getHolder();

			Location block = barrel.getLocation();
			Location target = ChestDailyMain.getInstance().getBarrelLocation();
			if (block.getBlockX() == target.getBlockX() && block.getBlockY() == target.getBlockY() && block.getBlockZ() == target.getBlockZ())
			{
				event.setCancelled(true);
				openInventoryFor(event.getPlayer());
			}
		}
	}
}
