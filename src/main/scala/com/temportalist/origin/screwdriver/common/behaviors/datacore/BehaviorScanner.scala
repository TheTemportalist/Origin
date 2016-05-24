package com.temportalist.origin.screwdriver.common.behaviors.datacore

import com.temportalist.origin.api.common.resource.EnumResource
import com.temportalist.origin.api.common.utility.Cursor
import com.temportalist.origin.screwdriver.api.{Behavior, BehaviorType}
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{EnumAction, ItemStack}
import net.minecraft.util.MovingObjectPosition.MovingObjectType
import net.minecraft.util.{MovingObjectPosition, ResourceLocation}
import net.minecraft.world.World

/**
 * Created by TheTemportalist on 12/24/2015.
 */
object BehaviorScanner extends Behavior("Scanner", true) {

	override def getBehaviorType: BehaviorType = BehaviorType.ACTIVE

	override def isValidStackForSimulation(stack: ItemStack): Boolean = false

	override def postInit(): Unit = {
		super.postInit()
		AddonScrewdriver.loadResource("scanner",
			(EnumResource.TEXTURE_ITEM, "moduleIcons/wireless.png"))
	}

	override protected def getTexture: ResourceLocation = AddonScrewdriver.getResource("scanner")

	override def onItemRightClick(container: ItemStack, source: ItemStack, world: World,
			player: EntityPlayer): ItemStack = {
		if (this.isValidTarget(container, Cursor.raytraceWorld(player))){
			player.setItemInUse(container, this.getMaxItemUseDuration(container, source))
			println("started scanning")
		}
		super.onItemRightClick(container, source, world, player)
	}

	override def getMaxItemUseDuration(container: ItemStack, source: ItemStack): Int = 32

	override def getItemUseAction(container: ItemStack, source: ItemStack): EnumAction = {
		//EnumHelper.addAction()
		EnumAction.none
	}

	override def onUsingTick(container: ItemStack, source: ItemStack, player: EntityPlayer,
			count: Int): Unit = {
		val validTarget = this.isValidTarget(container, Cursor.raytraceWorld(player))
		if (!validTarget || count <= 1) {
			player.stopUsingItem()
			if (validTarget && count <= 1) {
				val entity = Cursor.raytraceWorld(player).entityHit
				AddonScrewdriver.NBTBehaviorHelper.addScannedEntity(
					container, entity.getClass.getCanonicalName
				)
				player.inventory.setInventorySlotContents(player.inventory.currentItem, container)
				println("finished scanning")
			}
			else println("stopped scanning")
		}
		super.onUsingTick(container, source, player, count)
	}

	private def isValidTarget(stack: ItemStack, mop: MovingObjectPosition): Boolean = {
		mop != null && mop.typeOfHit == MovingObjectType.ENTITY &&
				mop.entityHit.isInstanceOf[EntityLivingBase] &&
				!AddonScrewdriver.NBTBehaviorHelper.getScannedEntityClassNames(stack).
						contains(mop.entityHit.getClass.getCanonicalName)
	}

}
