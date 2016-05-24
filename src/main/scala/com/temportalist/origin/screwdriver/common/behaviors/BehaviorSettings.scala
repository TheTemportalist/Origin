package com.temportalist.origin.screwdriver.common.behaviors

import com.temportalist.origin.api.common.resource.EnumResource
import com.temportalist.origin.screwdriver.api.{Behavior, BehaviorType}
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import net.minecraft.entity.player.{EntityPlayerMP, EntityPlayer}
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
 * Created by TheTemportalist on 12/21/2015.
 */
object BehaviorSettings extends Behavior("Settings", true) {

	override def isValidStackForSimulation(stack: ItemStack): Boolean = false

	override def postInit(): Unit = {
		super.postInit()
		AddonScrewdriver.loadResource("settings",
			(EnumResource.TEXTURE_ITEM, "moduleIcons/basicGear.png"))
	}

	override def getBehaviorType: BehaviorType = BehaviorType.ACTIVE

	override protected def getTexture: ResourceLocation = AddonScrewdriver.getResource("settings")

	override def onSelection(player: EntityPlayer): Boolean = {
		player match {
			case mp: EntityPlayerMP =>
				mp.openGui(AddonScrewdriver, AddonScrewdriver.GUI_BEHAVIORS, mp.worldObj,
					mp.posX.toInt, mp.posY.toInt, mp.posZ.toInt)
			case _ =>
		}
		false
	}

}
