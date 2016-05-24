package temportalist.origin.api.client.render

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.api.client.utility.Rendering

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class TERenderer[T <: TileEntity](private val texture: ResourceLocation)
		extends TileEntitySpecialRenderer[T] {

	def this() {
		this(null)
	}

	override def renderTileEntityAt(te: T, x: Double, y: Double, z: Double,
			partialTicks: Float, destroyStage: Int): Unit = {
		Rendering.push_gl()
		Rendering.translate_gl(x + 0.5, y + 0.5, z + 0.5)
		if (this.texture != null) Rendering.bindResource(this.texture)
		if (te != null) this.render(te, partialTicks, 0.0625F)
		Rendering.pop_gl()
	}

	protected def render(tileEntity: TileEntity, renderPartialTicks: Float, f5: Float): Unit = {}

}
