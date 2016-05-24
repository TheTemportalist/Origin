package temportalist.origin.screwdriver.common.behaviors

import com.temportalist.origin.screwdriver.api.{BehaviorSingleItem, BehaviorType}
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.util.ResourceLocation

/**
 * Created by TheTemportalist on 12/20/2015.
 */
object BehaviorShears extends BehaviorSingleItem("Shears", "minecraft:shears") {

	override def postInit(): Unit = {
		super.postInit()
		AddonScrewdriver.setResource("shears",
			new ResourceLocation("minecraft:textures/items/shears.png"))
	}

	override def shouldValidateMetadata(): Boolean = false

	override def getBehaviorType: BehaviorType = BehaviorType.ACTIVE

	@SideOnly(Side.CLIENT)
	override protected def getTexture: ResourceLocation = AddonScrewdriver.getResource("shears")

}
