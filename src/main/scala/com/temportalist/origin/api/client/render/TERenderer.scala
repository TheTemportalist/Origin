package com.temportalist.origin.api.client.render

import com.temportalist.origin.api.client.utility.Rendering
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class TERenderer(private val texture: ResourceLocation) extends TileEntitySpecialRenderer {

	def this() {
		this(null)
	}

	override def renderTileEntityAt(tileEntity: TileEntity, viewX: Double, viewY: Double,
			viewZ: Double, renderPartialTicks: Float): Unit = {
		GL11.glPushMatrix()
		GL11.glTranslated(viewX + 0.5, viewY + 0.5, viewZ + 0.5)

		if (this.texture != null)
			Rendering.bindResource(this.texture)

		if (tileEntity != null)
			this.render(tileEntity, renderPartialTicks, 0.0625F)

		GL11.glPopMatrix()
	}

	protected def render(tileEntity: TileEntity, renderPartialTicks: Float, f5: Float): Unit = {
	}

}
