package com.temportalist.origin.api.client.utility

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.renderer.Tessellator

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
object TessRenderer {

	def getTess(): Tessellator = Tessellator.instance

	//def getRenderer(): WorldRenderer = this.getTess().getWorldRenderer

	def startQuads(): Unit = this.getTess().startDrawingQuads()

	def setNormal(x: Float, y: Float, z: Float): Unit = {
		this.getTess().setNormal(x, y, z)
	}

	def draw(): Unit = this.getTess().draw()

	def addVertex(x: Double, y: Double, z: Double, u: Double, v: Double): Unit =
		this.getTess().addVertexWithUV(x, y, z, y, v)

	def addVertex(x: Double, y: Double, z: Double): Unit =
		this.getTess().addVertex(x, y, z)

}
