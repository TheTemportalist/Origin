package com.temportalist.origin.api.common.rendering

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author TheTemportalist
 */
trait ISpriteMapper {

	@SideOnly(Side.CLIENT)
	def getResourceLocation(): ResourceLocation

}
