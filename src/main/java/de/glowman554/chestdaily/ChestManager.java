package de.glowman554.chestdaily;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.glowman554.chestdaily.utils.AutoResettingArrayList;

public class ChestManager implements Listener
{
	private AutoResettingArrayList<UUID> cooldown = new AutoResettingArrayList<UUID>("chests", UUID::fromString);
	private HashMap<String, Inventory> inventories = new HashMap<>();

	private Random random = new Random();

	public ChestManager()
	{
		cooldown.hook(this::onReset);
	}

	private void onReset()
	{
		inventories.clear();
	}

	public void forceReset()
	{
		cooldown.forceReset();
	}

	private void openInventoryFor(HumanEntity player)
	{
		if (inventories.get(player.getUniqueId().toString()) == null)
		{
			Inventory inventory = Bukkit.createInventory(player, 9 * 3, "§c§lLootkiste");
			inventories.put(player.getUniqueId().toString(), inventory);

			if (!cooldown.contains(player.getUniqueId()))
			{
				int slotsToFill = random.nextInt(3, 11);
				for (int i = 0; i < slotsToFill; i++)
				{
					int slot = random.nextInt(0, 9 * 3);
					ChestItem item = ChestDailyMain.getInstance().getChestItems()[random.nextInt(0, ChestDailyMain.getInstance().getChestItems().length)];

					inventory.setItem(slot, new ItemStack(item.getItem(), item.getAmmount()));
				}
				cooldown.add(player.getUniqueId());
				cooldown.save();
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
