package temportalist.origin.screwdriver.common.behaviors.datacore

import com.temportalist.origin.api.common.resource.EnumResource
import com.temportalist.origin.screwdriver.api.{BehaviorType, Behavior}
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
 * Created by TheTemportalist on 12/25/2015.
 */
object BehaviorDataCore extends Behavior("Data Core", true) {

	override def getBehaviorType: BehaviorType = BehaviorType.ACTIVE

	override def isValidStackForSimulation(stack: ItemStack): Boolean = false

	override def postInit(): Unit = {
		super.postInit()
		AddonScrewdriver.loadResource("datacore",
			(EnumResource.TEXTURE_ITEM,  "moduleIcons/datacore.png"))
	}

	override protected def getTexture: ResourceLocation = AddonScrewdriver.getResource("datacore")

	override def onSelection(player: EntityPlayer): Boolean = {
		player.openGui(AddonScrewdriver, AddonScrewdriver.GUI_DATA_CORE, player.worldObj,
			player.posX.toInt, player.posY.toInt, player.posZ.toInt)
		false
	}

}
