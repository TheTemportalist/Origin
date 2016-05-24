package temportalist.origin.screwdriver.common.behaviors

import com.temportalist.origin.screwdriver.api.{BehaviorSingleItem, BehaviorType}
import com.temportalist.origin.screwdriver.common.{AddonScrewdriver, CompatibleAPI}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.util.ResourceLocation

/**
 * Created by TheTemportalist on 12/20/2015.
 */
object BehaviorMFRHighlight extends BehaviorSingleItem("MFR Area Of Effect",
	CompatibleAPI.MFR.getModid + ":hammer") {

	@SideOnly(Side.CLIENT)
	override protected def getTexture: ResourceLocation = AddonScrewdriver.getResource("mfrHammer")

	override def postInit(): Unit = {
		super.postInit()
		AddonScrewdriver.setResource("mfrHammer",
			new ResourceLocation("minefactoryreloaded:textures/items/item.mfr.hammer.png"))
	}

	override def shouldValidateMetadata(): Boolean = false

	def getBehaviorType: BehaviorType = BehaviorType.PASSIVE

	override def equals(obj: scala.Any): Boolean = {
		try {
			/* NOTES:
			The reason this had to be reflected is as follows:
			MineFactorReloaded, in order to render the translucent area of effect, checks if the
			item the player is holder equals their hammer item.
			See: https://github.com/skyboy/MineFactoryReloaded/blob/babd3f04acbd98e4113b0a70a648649a7a29b5bb/src/powercrystals/minefactoryreloaded/MineFactoryReloadedClient.java#L446
			In order to implement this functionality, the screwdriver must "equal" that hammer,
			hence the reflection in order to get the hammer item. The field for this item located
			at: https://github.com/skyboy/MineFactoryReloaded/blob/71cbc35f34ba0a5c3756ee24129e7e0129664193/src/powercrystals/minefactoryreloaded/setup/MFRThings.java#L78
			 */
			if (this.getTemplateItemStack != null && this.getTemplateItemStack.getItem.equals(obj)) return true
		}
		catch {
			case e: Exception =>
		}
		super.equals(obj)
	}
}
