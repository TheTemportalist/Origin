package temportalist.origin.screwdriver.common.behaviors.enderio

import com.temportalist.origin.api.common.resource.EnumResource
import com.temportalist.origin.screwdriver.api.{Behavior, BehaviorType}
import com.temportalist.origin.screwdriver.common.{AddonScrewdriver, CompatibleAPI}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
 * Created by TheTemportalist on 12/20/2015.
 */
object BehaviorEnderIOConduitVisibility extends Behavior("Conduit Visibility") {

	override def isValidStackForSimulation(stack: ItemStack): Boolean = {
		if (CompatibleAPI.ENDERIO.isAPILoaded) {
			if (stack.getItem.isInstanceOf[crazypants.enderio.api.tool.IConduitControl]) return true
		}
		false
	}

	override def postInit(): Unit = {
		super.postInit()
		AddonScrewdriver.loadResource("conduit",
			(EnumResource.TEXTURE_ITEM, "moduleIcons/conduit.png"))
	}

	def getBehaviorType: BehaviorType = BehaviorType.PASSIVE

	@SideOnly(Side.CLIENT)
	override protected def getTexture: ResourceLocation = AddonScrewdriver.getResource("conduit")

}
