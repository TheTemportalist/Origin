package temportalist.origin.api.common.rendering

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{SideOnly, Side}

/**
 *
 *
 * @author TheTemportalist
 */
trait ISpriteMapper {

	@SideOnly(Side.CLIENT)
	def getResourceLocation: ResourceLocation

}
