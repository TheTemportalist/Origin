package com.temportalist.origin.screwdriver.api;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

/**
 * Created by TheTemportalist on 12/22/2015.
 */
public abstract class BehaviorSingleItem extends Behavior {

	private static ItemStack getItemStackFromName(String name) {
		if (!name.matches("(.*):(.*)")) return null;
		int endNameIndex = name.length();
		int metadata = OreDictionary.WILDCARD_VALUE;

		if (name.matches("(.*):(.*):(.*)")) {
			endNameIndex = name.lastIndexOf(':');
			metadata = Integer.parseInt(name.substring(endNameIndex + 1, name.length()));
		}

		String modid = name.substring(0, name.indexOf(':')),
				itemName = name.substring(name.indexOf(':') + 1, endNameIndex);
		Block block = GameRegistry.findBlock(modid, itemName);
		Item item = GameRegistry.findItem(modid, itemName);
		return (block != null && Item.getItemFromBlock(block) != null)
				? new ItemStack(block, 1, metadata)
				: (item != null)
				? new ItemStack(item, 1, metadata)
				: null;
	}

	private ItemStack itemStack;
	private String itemStackName;

	public BehaviorSingleItem(String name, ItemStack stack) {
		this(name, false, stack);
	}

	public BehaviorSingleItem(String name, String stackQualifier) {
		this(name, false, stackQualifier);
	}

	public BehaviorSingleItem(String name, boolean isDefaultBehavior, ItemStack stack) {
		super(name, isDefaultBehavior);
		this.itemStack = stack;
	}

	public BehaviorSingleItem(String name, boolean isDefaultBehavior, String stackQualifier) {
		super(name, isDefaultBehavior);
		this.itemStackName = stackQualifier;
	}

	@Override
	public void postInit() {
		super.postInit();
		if (this.itemStack == null)
			this.itemStack = BehaviorSingleItem.getItemStackFromName(this.itemStackName);
	}

	protected ItemStack getTemplateItemStack() {
		if (this.itemStack == null)
			FMLLog.log("Origin Screwdriver API", Level.ERROR,
					("Error! Internal stack for behavior " + this.getName() + " is null! " +
							this.getClass().getCanonicalName()));
		return this.itemStack;
	}

	@Override
	public boolean isValidStackForSimulation(ItemStack stack) {
		return this.itemStack != null && stack != null &&
				this.itemStack.getItem() == stack.getItem() &&
				(!this.shouldValidateMetadata() ||
						this.itemStack.getItemDamage() == stack.getItemDamage()) &&
				ItemStack.areItemStackTagsEqual(this.itemStack, stack);
	}

	public boolean shouldValidateMetadata() {
		return this.itemStack != null &&
				this.itemStack.getItemDamage() != OreDictionary.WILDCARD_VALUE;
	}

	@Override
	public boolean onItemUseFirst(ItemStack container, ItemStack source, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return source != null && source.getItem().onItemUseFirst(source, player, world, x, y, z,
				side, hitX, hitY, hitZ);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack container, ItemStack source, EntityPlayer player,
			EntityLivingBase entity) {
		return source != null && source.getItem().itemInteractionForEntity(source, player, entity);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack container, ItemStack source, int x, int y, int z,
			EntityPlayer player) {
		return source != null && source.getItem().onBlockStartBreak(source, x, y, z, player);
	}

}
