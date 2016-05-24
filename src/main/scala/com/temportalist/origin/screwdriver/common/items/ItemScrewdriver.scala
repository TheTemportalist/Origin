package com.temportalist.origin.screwdriver.common.items

import java.util

import com.temportalist.origin.api.common.item.ItemBase
import com.temportalist.origin.api.common.utility.{Generic, Stacks}
import com.temportalist.origin.screwdriver.api.Behavior
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import com.temportalist.origin.screwdriver.common.behaviors._
import com.temportalist.origin.screwdriver.common.behaviors.enderio.{BehaviorEnderIOFacadeVisibility, IEnderIOConduitVisibility, IEnderIOFacadeVisibility}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{EnumAction, Item, ItemStack}
import net.minecraft.util.IIcon
import net.minecraft.world.World

/**
 * Created by TheTemportalist on 12/20/2015.
 */
class ItemScrewdriver extends ItemBase(AddonScrewdriver.getModid, "screwdriver")
with IWrench with IRailcraftCrowbar
with IEnderIOFacadeVisibility with IEnderIOConduitVisibility {

	this.setHasSubtypes(true)

	/**
	 * allows items to add custom lines of information to the mouseover description
	 *
	 * @param itemStack
	 * @param player
	 * @param list
	 * @param isAdvanced
	 */
	@SideOnly(Side.CLIENT)
	override def addInformation(itemStack: ItemStack, player: EntityPlayer, list: util.List[_],
			isAdvanced: Boolean): Unit = {
		Generic.addToList(list, "This could be a little more sonic")
	}

	@SideOnly(Side.CLIENT)
	private var icons: Array[IIcon] = _

	@SideOnly(Side.CLIENT)
	override def registerIcons(reg: IIconRegister): Unit = {
		this.icons = new Array[IIcon](AddonScrewdriver.screwdrivers.length)
		for (i <- this.icons.indices)
			this.icons(i) = reg.registerIcon(this.modid + ":screwdriver_" + i)
	}

	@SideOnly(Side.CLIENT)
	override def getIconFromDamage(meta: Int): IIcon = this.icons(meta)

	@SideOnly(Side.CLIENT)
	override def getSubItems(item: Item, tab: CreativeTabs, list: util.List[_]): Unit = {
		AddonScrewdriver.screwdrivers.foreach(stack => Generic.addToList(list, stack))
	}

	private def getActiveBehavior(stack: ItemStack): Behavior = {
		if (stack.getItemDamage > 0)
			AddonScrewdriver.NBTBehaviorHelper.getCurrentActiveBehavior(stack)
		else null
	}

	override def canWrench(stack: ItemStack): Boolean =
		this.getActiveBehavior(stack) == BehaviorWrench

	override def canUseCrowbar(stack: ItemStack): Boolean =
		this.getActiveBehavior(stack) == BehaviorRailcraftCrowbar

	override def canHideFacades(stack: ItemStack): Boolean =
		AddonScrewdriver.NBTBehaviorHelper.isToggledEnabled(stack, BehaviorEnderIOFacadeVisibility)

	private def getFirstStackThatMatchesBehavior(
			stack: ItemStack, behavior: Behavior): (Int, ItemStack) = {
		if (stack.getItemDamage > 0)
			AddonScrewdriver.NBTBehaviorHelper.getFirstStackThatMatches(stack, behavior)
		else (-1, null)
	}

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on sheep.
	 *
	 * @param screwdriver
	 * @param player
	 * @param entity
	 * @return
	 */
	override def itemInteractionForEntity(screwdriver: ItemStack, player: EntityPlayer,
			entity: EntityLivingBase): Boolean = {
		if (screwdriver.getItemDamage < 1) return false
		val behavior = this.getActiveBehavior(screwdriver)
		if (behavior != null) {
			val source = this.getFirstStackThatMatchesBehavior(screwdriver, behavior)
			if (source._2 != null) {
				val sourceStackCopy = source._2.copy()
				val ret = behavior.itemInteractionForEntity(
					screwdriver, sourceStackCopy, player, entity)
				if (!Stacks.doStacksMatch(source._2, sourceStackCopy, nbt = true, nil = false))
					this.updateModule(player, screwdriver, source._1, sourceStackCopy)
				return ret
			}
			else if (behavior.isDefaultBehavior) {
				return behavior.itemInteractionForEntity(screwdriver, screwdriver, player, entity)
			}
			else return false
		}
		false
	}

	override def onBlockStartBreak(screwdriver: ItemStack, x: Int, y: Int, z: Int,
			player: EntityPlayer): Boolean = {
		if (screwdriver.getItemDamage < 1) return false
		val behavior = this.getActiveBehavior(screwdriver)
		if (behavior != null) {
			val source = this.getFirstStackThatMatchesBehavior(screwdriver, behavior)
			if (source._2 != null) {
				val sourceStackCopy = source._2.copy()
				val ret = behavior.onBlockStartBreak(screwdriver, sourceStackCopy, x, y, z, player)
				if (!Stacks.doStacksMatch(source._2, sourceStackCopy, nbt = true, nil = false))
					this.updateModule(player, screwdriver, source._1, sourceStackCopy)
				return ret
			}
			else if (behavior.isDefaultBehavior) {
				return behavior.onBlockStartBreak(screwdriver, screwdriver, x, y, z, player)
			}
			else return false
		}
		false
	}

	override def onItemUseFirst(screwdriver: ItemStack, player: EntityPlayer, world: World, x: Int,
			y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		if (screwdriver.getItemDamage < 1) return false
		val behavior = this.getActiveBehavior(screwdriver)
		if (behavior != null) {
			val source = this.getFirstStackThatMatchesBehavior(screwdriver, behavior)
			if (source._2 != null) {
				val sourceStackCopy = source._2.copy()
				val ret = behavior.onItemUseFirst(screwdriver, sourceStackCopy, player, world,
					x, y, z, side, hitX, hitY, hitZ)
				if (!Stacks.doStacksMatch(source._2, sourceStackCopy, nbt = true, nil = false))
					this.updateModule(player, screwdriver, source._1, sourceStackCopy)
				return ret
			}
			else if (behavior.isDefaultBehavior) {
				return behavior.onItemUseFirst(screwdriver, screwdriver, player, world,
					x, y, z, side, hitX, hitY, hitZ)
			}
			else return false
		}
		false
	}

	override def getToolClasses(screwdriver: ItemStack): util.Set[String] = {
		if (screwdriver.getItemDamage < 1) return super.getToolClasses(screwdriver)
		val set = new util.HashSet[String]()
		set.addAll(super.getToolClasses(screwdriver))

		val behavior = this.getActiveBehavior(screwdriver)
		if (behavior != null) {
			val source = this.getFirstStackThatMatchesBehavior(screwdriver, behavior)
			if (source._2 != null) behavior.getBehaviorToolClasses(screwdriver, source._2, set)
			else if (behavior.isDefaultBehavior)
				behavior.getBehaviorToolClasses(screwdriver, screwdriver, set)
		}

		set
	}

	private def updateModule(player: EntityPlayer, container: ItemStack,
			index: Int, module: ItemStack): Unit = {
		if (!AddonScrewdriver.NBTBehaviorHelper.isStackValidAsModule(module)) return
		AddonScrewdriver.NBTBehaviorHelper.setModule(container, index, module)
		player.inventory.setInventorySlotContents(player.inventory.currentItem, container)
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed.
	 *
	 * @param screwdriver
	 * @param world
	 * @param player
	 * @return
	 */
	override def onItemRightClick(screwdriver: ItemStack, world: World,
			player: EntityPlayer): ItemStack = {
		if (screwdriver.getItemDamage < 1) return screwdriver
		val behavior = this.getActiveBehavior(screwdriver)
		if (behavior != null) {
			val source = this.getFirstStackThatMatchesBehavior(screwdriver, behavior)
			if (source._2 != null) {
				var sourceStackCopy = source._2.copy()
				sourceStackCopy = behavior
						.onItemRightClick(screwdriver, sourceStackCopy, world, player)
				if (!Stacks.doStacksMatch(source._2, sourceStackCopy, nbt = true, nil = false))
					this.updateModule(player, screwdriver, source._1, sourceStackCopy)
			}
			else if (behavior.isDefaultBehavior) {
				behavior.onItemRightClick(screwdriver, screwdriver, world, player)
			}
		}
		screwdriver
	}

	override def getMaxItemUseDuration(screwdriver: ItemStack): Int = {
		if (screwdriver.getItemDamage < 1) return 0
		val behavior = this.getActiveBehavior(screwdriver)
		if (behavior != null) {
			val source = this.getFirstStackThatMatchesBehavior(screwdriver, behavior)
			return behavior.getMaxItemUseDuration(screwdriver,
				if (source._2 == null) screwdriver else source._2)
		}
		0
	}

	override def getItemUseAction(screwdriver: ItemStack): EnumAction = {
		if (screwdriver.getItemDamage < 1) return EnumAction.none
		val behavior = this.getActiveBehavior(screwdriver)
		if (behavior != null) {
			val source = this.getFirstStackThatMatchesBehavior(screwdriver, behavior)
			return behavior.getItemUseAction(screwdriver,
				if (source._2 == null) screwdriver else source._2)
		}
		EnumAction.none
	}

	override def onUsingTick(screwdriver: ItemStack, player: EntityPlayer, count: Int): Unit = {
		if (screwdriver.getItemDamage < 1) return
		val behavior = this.getActiveBehavior(screwdriver)
		if (behavior != null) {
			val source = this.getFirstStackThatMatchesBehavior(screwdriver, behavior)
			behavior.onUsingTick(screwdriver,
				if (source._2 == null) screwdriver else source._2, player, count)
		}
	}

	override def onPlayerStoppedUsing(screwdriver: ItemStack, world: World,
			player: EntityPlayer, itemInUseCount: Int): Unit = {
		if (screwdriver.getItemDamage < 1) return
		val behavior = this.getActiveBehavior(screwdriver)
		if (behavior != null) {
			val source = this.getFirstStackThatMatchesBehavior(screwdriver, behavior)
			if (source._2 != null) {
				val sourceStackCopy = source._2.copy()
				behavior.onPlayerStoppedUsing(screwdriver, sourceStackCopy, world, player,
					itemInUseCount)
				if (!Stacks.doStacksMatch(source._2, sourceStackCopy, nbt = true, nil = false))
					this.updateModule(player, screwdriver, source._1, sourceStackCopy)
			}
			else if (behavior.isDefaultBehavior) {
				behavior.onPlayerStoppedUsing(screwdriver, screwdriver, world, player,
					itemInUseCount)
			}
		}
	}

}
