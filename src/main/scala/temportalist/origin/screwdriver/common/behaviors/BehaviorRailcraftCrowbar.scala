package temportalist.origin.screwdriver.common.behaviors

import com.temportalist.origin.screwdriver.api.{Behavior, BehaviorType}
import com.temportalist.origin.screwdriver.common.{AddonScrewdriver, CompatibleAPI}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
 * Created by TheTemportalist on 12/20/2015.
 */
object BehaviorRailcraftCrowbar extends Behavior("Crowbar") {

	override def isValidStackForSimulation(stack: ItemStack): Boolean = {
		if (CompatibleAPI.RAILCRAFT.isAPILoaded) {
			return stack.getItem.isInstanceOf[mods.railcraft.api.core.items.IToolCrowbar]
		}
		false
	}

	override def postInit(): Unit = {
		super.postInit()
		AddonScrewdriver.setResource("crowbar",
			new ResourceLocation("railcraft:textures/items/tool.crowbar.png"))
	}

	def getBehaviorType: BehaviorType = BehaviorType.ACTIVE

	@SideOnly(Side.CLIENT)
	override protected def getTexture: ResourceLocation = AddonScrewdriver.getResource("crowbar")

}
