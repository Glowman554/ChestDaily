package de.glowman554.chestdaily;

import org.bukkit.Material;

public class ChestItem
{
	private final Material item;
	private final int ammount;

	public ChestItem(Material item, int ammount)
	{
		this.item = item;
		this.ammount = ammount;
	}

	public int getAmmount()
	{
		return ammount;
	}

	public Material getItem()
	{
		return item;
	}

	@Override
	public String toString()
	{
		return String.format("ChestItem{item=%s, ammount=%d}", item, ammount);
	}
}
