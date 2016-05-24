package temportalist.origin.screwdriver.common.behaviors.immersiveengineering

import com.temportalist.origin.screwdriver.api.{BehaviorSingleItem, BehaviorType}
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.util.ResourceLocation

/**
 * Created by TheTemportalist on 12/21/2015.
 */
object BehaviorIEWireCutter extends BehaviorSingleItem("Wire Cutter", "ImmersiveEngineering:tool:1") {

	override def getBehaviorType: BehaviorType = BehaviorType.ACTIVE

	override def postInit(): Unit = {
		super.postInit()
		AddonScrewdriver.setResource("iewirecutter", new ResourceLocation(
			"immersiveengineering:textures/items/tool_wirecutter.png"))
	}

	@SideOnly(Side.CLIENT)
	override protected def getTexture: ResourceLocation = AddonScrewdriver.getResource("iewirecutter")

}
