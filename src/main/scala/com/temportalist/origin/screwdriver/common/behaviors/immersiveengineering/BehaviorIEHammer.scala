package com.temportalist.origin.screwdriver.common.behaviors.immersiveengineering

import com.temportalist.origin.screwdriver.api.{BehaviorSingleItem, BehaviorType}
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World

/**
 * Created by TheTemportalist on 12/20/2015.
 */
object BehaviorIEHammer extends BehaviorSingleItem("Hammer", "ImmersiveEngineering:tool:0") {

	override def postInit(): Unit = {
		super.postInit()
		AddonScrewdriver.setResource("iehammer", new ResourceLocation(
			"immersiveengineering:textures/items/tool_hammer.png"))
	}

	override def getBehaviorType: BehaviorType = BehaviorType.ACTIVE

	override protected def getTexture: ResourceLocation = AddonScrewdriver.getResource("iehammer")

	def dismantle(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Boolean = {
		if (this.getTemplateItemStack != null &&
				this.getTemplateItemStack.getItem.getDigSpeed(this.getTemplateItemStack,
					world.getBlock(x, y, z), world.getBlockMetadata(x, y, z)) > 1)
			player match {
				case mp: EntityPlayerMP =>
					return mp.theItemInWorldManager.tryHarvestBlock(x, y, z)
				case _ =>
			}
		false
	}

}
