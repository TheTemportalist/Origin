package temportalist.origin.screwdriver.common.behaviors

import com.temportalist.origin.api.common.item.ItemBase
import cpw.mods.fml.common.Optional
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World

/**
 * Created by TheTemportalist on 12/20/2015.
 */
@Optional.InterfaceList(Array[Optional.Interface](
	new Optional.Interface(
		iface = "buildcraft.api.tools.IToolWrench", modid = "BuildCraft|Core", striprefs = true),
	new Optional.Interface(
		iface = "cofh.api.item.IToolHammer", modid = "CoFHLib", striprefs = true),
	new Optional.Interface(
		iface = "powercrystals.minefactoryreloaded.api.IMFRHammer", modid = "MineFactoryReloaded",
		striprefs = true),
	new Optional.Interface(
		iface = "crazypants.enderio.api.tool.ITool", modid = "EnderIO", striprefs = true),
	new Optional.Interface(
		iface = "appeng.api.implementations.items.IAEWrench", modid = "appliedenergistics2",
		striprefs = true)
))
trait IWrench extends ItemBase
		with buildcraft.api.tools.IToolWrench
		with cofh.api.item.IToolHammer
		with powercrystals.minefactoryreloaded.api.IMFRHammer
		with crazypants.enderio.api.tool.ITool
		with appeng.api.implementations.items.IAEWrench {

	def canWrench(stack: ItemStack): Boolean

	override def doesSneakBypassUse(world: World, x: Int, y: Int, z: Int,
			player: EntityPlayer): Boolean = true

	// ~~~~~~~~~~~ canWrench methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Buildcraft IToolWrench
	@Optional.Method(modid = "BuildCraft|Core")
	override def canWrench(player: EntityPlayer, x: Int, y: Int, z: Int): Boolean =
		canWrench(player.getCurrentEquippedItem)

	// CoFH IToolHammer
	@Optional.Method(modid = "CoFHLib")
	override def isUsable(stack: ItemStack, user: EntityLivingBase,
			x: Int, y: Int, z: Int): Boolean = canWrench(stack)

	// EnderIO ITool
	@Optional.Method(modid = "EnderIO")
	override def canUse(stack: ItemStack, player: EntityPlayer,
			x: Int, y: Int, z: Int): Boolean = canWrench(stack)

	// Applied Energistics IAEWrench
	@Optional.Method(modid = "appliedenergistics2")
	override def canWrench(stack: ItemStack, player: EntityPlayer,
			x: Int, y: Int, z: Int): Boolean = canWrench(stack)

	// ~~~~~~~~~~~ Post-Wrench methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Buildcraft IToolWrench
	@Optional.Method(modid = "BuildCraft|Core")
	override def wrenchUsed(player: EntityPlayer, x: Int, y: Int, z: Int): Unit = {}

	// CoFH IToolHammer
	@Optional.Method(modid = "CoFHLib")
	override def toolUsed(item: ItemStack, user: EntityLivingBase,
			x: Int, y: Int, z: Int): Unit = {}
	
	// EnderIO ITool
	@Optional.Method(modid = "EnderIO")
	override def used(stack: ItemStack, player: EntityPlayer, x: Int, y: Int, z: Int): Unit = {}

}
