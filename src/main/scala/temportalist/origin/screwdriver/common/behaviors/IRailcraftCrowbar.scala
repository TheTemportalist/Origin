package temportalist.origin.screwdriver.common.behaviors

import cpw.mods.fml.common.Optional
import net.minecraft.entity.item.EntityMinecart
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

/**
 * Created by TheTemportalist on 12/20/2015.
 */
@Optional.Interface(
	iface = "mods.railcraft.api.core.items.IToolCrowbar", modid = "Railcraft", striprefs = true)
trait IRailcraftCrowbar extends mods.railcraft.api.core.items.IToolCrowbar {

	def canUseCrowbar(stack: ItemStack): Boolean

	@Optional.Method(modid = "Railcraft")
	override def canWhack(player: EntityPlayer, stack: ItemStack,
			x: Int, y: Int, z: Int): Boolean = this.canUseCrowbar(stack)

	@Optional.Method(modid = "Railcraft")
	override def canLink(player: EntityPlayer, stack: ItemStack,
			cart: EntityMinecart): Boolean = this.canUseCrowbar(stack)

	@Optional.Method(modid = "Railcraft")
	override def canBoost(player: EntityPlayer, stack: ItemStack,
			cart: EntityMinecart): Boolean = this.canUseCrowbar(stack)

	@Optional.Method(modid = "Railcraft")
	override def onWhack(player: EntityPlayer, crowbar: ItemStack,
			x: Int, y: Int, z: Int): Unit = {}

	@Optional.Method(modid = "Railcraft")
	override def onLink(player: EntityPlayer, crowbar: ItemStack, cart: EntityMinecart): Unit = {}

	@Optional.Method(modid = "Railcraft")
	override def onBoost(player: EntityPlayer, crowbar: ItemStack, cart: EntityMinecart): Unit = {}

}
