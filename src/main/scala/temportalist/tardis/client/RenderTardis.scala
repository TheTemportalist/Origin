package temportalist.tardis.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import temportalist.origin.api.client.TessRenderer
import temportalist.tardis.common.Tardis
import temportalist.tardis.common.entity.EntityTardis

/**
  *
  * Created by TheTemportalist on 5/27/2016.
  *
  * @author TheTemportalist
  */
class RenderTardis(manager: RenderManager) extends Render[EntityTardis](manager) {

	override def getEntityTexture(entity: EntityTardis): ResourceLocation = {
		null
	}

	override def doRender(entity: EntityTardis, x: Double, y: Double, z: Double,
			entityYaw: Float, partialTicks: Float): Unit = {

		/*
		val stone = Blocks.STONE.getDefaultState
		Minecraft.getMinecraft.getBlockRendererDispatcher.renderBlock(stone,
			new BlockPos(x.toInt, y.toInt, z.toInt), entity.getEntityWorld, TessRenderer.getBuffer
		)
		*/

		super.doRender(entity, x, y, z, entityYaw, partialTicks)
	}

}
