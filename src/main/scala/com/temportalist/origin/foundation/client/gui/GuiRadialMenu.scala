package com.temportalist.origin.foundation.client.gui

import java.util

import com.temportalist.origin.api.client.gui.IGuiScreen
import com.temportalist.origin.api.client.utility.TessRenderer
import com.temportalist.origin.api.common.lib.IRadialSelection
import com.temportalist.origin.api.common.register.Registry
import com.temportalist.origin.foundation.client.gui.GuiRadialMenu.RadialHandler
import com.temportalist.origin.foundation.common.network.PacketTriggerRadialSelection
import com.temportalist.origin.internal.common.Origin
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.{GuiButton, ScaledResolution}
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11

import scala.collection.mutable.ListBuffer

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
abstract class GuiRadialMenu[T <: IRadialSelection](
		private val innerRadius: Int, private val outerRadius: Int,
		private val selections: Array[T]
		) extends IGuiScreen {

	this.setSize(outerRadius * 2, outerRadius * 2)

	val animationTimer: Int = 0

	var selectedLocalIndex: Int = -1

	def shouldSelect(): Boolean

	def selectCurrent(): Unit = {
		this.dismiss()
		if (this.selectedLocalIndex >= 0) {
			this.onSelectionOf(this.selectedLocalIndex, this.selections(this.selectedLocalIndex))
		}
	}

	def onSelectionOf(index: Int, item: T): Unit = {
		if (item != null) {
			item.onSelection(this.mc.thePlayer)
			val handlerID = GuiRadialMenu.getHandlerID(this.getHandler)
			if (handlerID >= 0)
				new PacketTriggerRadialSelection(handlerID, item).sendToServer(Origin)
		}
	}

	def getHandler: RadialHandler[T]

	def renderMenu(): Unit = {
		val zLevel: Double = 0.05D
		val resolution: ScaledResolution = new ScaledResolution(
			this.mc, this.mc.displayWidth, this.mc.displayHeight
		)

		val anglePerSection: Double = 360D / this.selections.length.toDouble
		this.renderRadial(resolution, zLevel, this.selections.length, anglePerSection)
		this.renderIconsAndText(resolution, zLevel, this.selections.length, anglePerSection)

	}

	def renderRadial(resolution: ScaledResolution, zLevel: Double,
			quantity: Int, anglePer: Double): Unit = {
		GL11.glPushMatrix()

		GL11.glDisable(GL11.GL_TEXTURE_2D)

		//GlStateManager.enableBlend()
		GL11.glEnable(GL11.GL_BLEND)
		//GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

		/*
		GlStateManager.matrixMode(GL11.GL_MODELVIEW)
		GlStateManager.pushMatrix()
		GlStateManager.loadIdentity()
		*/
		///*
		GL11.glMatrixMode(GL11.GL_MODELVIEW)
		GL11.glPushMatrix()
		GL11.glLoadIdentity()
		//*/

		/*
		GlStateManager.matrixMode(GL11.GL_PROJECTION)
		GlStateManager.pushMatrix()
		GlStateManager.loadIdentity()
		*/
		///*
		GL11.glMatrixMode(GL11.GL_PROJECTION)
		GL11.glPushMatrix()
		GL11.glLoadIdentity()
		//*/

		val mouseAngle = this.correctAngle(this.getMouseAngle - 270)

		for (i <- 0 until quantity) {
			var currAngle: Double = anglePer * i
			var nextAngle: Double = currAngle + anglePer
			currAngle = this.correctAngle(currAngle)
			nextAngle = this.correctAngle(nextAngle)

			val isMouseIn: Boolean = mouseAngle > currAngle && mouseAngle < nextAngle

			currAngle = Math.toRadians(currAngle)
			nextAngle = Math.toRadians(nextAngle)

			val innerR: Double =
				((this.innerRadius - animationTimer - (if (isMouseIn) 1 else 2)) / 100F) *
						(257F / resolution.getScaledHeight.toFloat)
			val outerR: Double =
				((this.outerRadius - animationTimer + (if (isMouseIn) 1 else 2)) / 100F) *
						(257F / resolution.getScaledHeight.toFloat)

			TessRenderer.startQuads()

			///*
			if (isMouseIn) {
				TessRenderer.getTess().setColorRGBA_F(
					28F / 255F, 232F / 255F, 31F / 255F, 153F / 255F
				)
				this.selectedLocalIndex = i
			}
			else {
				TessRenderer.getTess().setColorRGBA_F(
					0F / 255F, 0F / 255F, 0F / 255F, 153F / 255F
				)
			}
			//*/

			TessRenderer.addVertex(
				Math.cos(currAngle) *
						resolution.getScaledHeight_double /
						resolution.getScaledWidth_double *
						innerR,
				Math.sin(currAngle) * innerR, 0
			)
			TessRenderer.addVertex(
				Math.cos(currAngle) *
						resolution.getScaledHeight_double /
						resolution.getScaledWidth_double *
						outerR,
				Math.sin(currAngle) * outerR, 0
			)
			TessRenderer.addVertex(
				Math.cos(nextAngle) * resolution.getScaledHeight_double /
						resolution.getScaledWidth_double * outerR,
				Math.sin(nextAngle) * outerR, 0
			)
			TessRenderer.addVertex(
				Math.cos(nextAngle) * resolution.getScaledHeight_double /
						resolution.getScaledWidth_double * innerR,
				Math.sin(nextAngle) * innerR, 0
			)

			TessRenderer.draw()

		}

		/*
		GlStateManager.popMatrix()
		GlStateManager.matrixMode(GL11.GL_MODELVIEW)
		GlStateManager.popMatrix()

		GlStateManager.disableBlend()

		GlStateManager.enableTexture2D()

		GlStateManager.popMatrix()
		*/
		///*
		GL11.glPopMatrix()
		GL11.glMatrixMode(GL11.GL_MODELVIEW)
		GL11.glPopMatrix()
		GL11.glDisable(GL11.GL_BLEND)
		GL11.glEnable(GL11.GL_TEXTURE_2D)
		GL11.glPopMatrix()
		//*/

	}

	def renderIconsAndText(resolution: ScaledResolution, zLevel: Double,
			quantity: Int, anglePer: Double): Unit = {
		//GlStateManager.pushMatrix()
		GL11.glPushMatrix()

		GL11.glTranslated(
			resolution.getScaledWidth_double / 2,
			resolution.getScaledHeight_double / 2, 0
		)

		var selection: IRadialSelection = null
		for (i <- 0 until quantity) {
			selection = this.selections(i)
			if (selection != null) {
				val angle: Double = (anglePer * i * -1) - anglePer / 2
				val drawOffset: Double = 2.0D
				var drawX: Double = this.innerRadius - this.animationTimer + drawOffset
				var drawY: Double = this.innerRadius - this.animationTimer + drawOffset
				val length: Double = Math.sqrt(drawX * drawX + drawY + drawY)

				val size = 32

				drawX = length * Math.cos(StrictMath.toRadians(angle))
				drawY = length * Math.sin(StrictMath.toRadians(angle))
				val dif: Double = this.outerRadius.toDouble / this.innerRadius.toDouble
				val iconX: Double = drawX * dif * 0.7D - (size / 2)
				val iconY: Double = drawY * dif * 0.7D - (size / 2)

				selection.draw(this.mc, iconX, iconY, zLevel, size, size, true)
			}
		}

		GL11.glPopMatrix()
	}

	def getMouseAngle: Double = {
		getRelativeAngle(
			this.mc.displayWidth / 2, this.mc.displayHeight / 2, Mouse.getX, Mouse.getY
		)
	}

	def getRelativeAngle(originX: Double, originY: Double, x: Double, y: Double): Double = {
		var angle: Double = Math.toDegrees(Math.atan2(y - originY, x - originX))

		// Remove 90 from the angle to make 0 and 180 at the top and bottom of the screen
		angle = angle - 90

		if (angle < 0) {
			angle = angle + 360
		}
		else if (angle > 360) {
			angle = angle - 360
		}

		angle
	}

	def correctAngle(angle: Double): Double = {
		var angle2: Double = angle
		if (angle < 0) {
			angle2 = angle + 360
		}
		else if (angle > 360) {
			angle2 = angle - 360
		}

		angle2
	}

	final def dismiss(): Unit = {
		this.mc.displayGuiScreen(null)
	}

	override def drawScreen(mouseX: Int, mouseY: Int, renderPartialTicks: Float): Unit = {}

	override protected def addButton(button: GuiButton): Unit = {}

	override protected def renderHoverInformation(mouseX: Int, mouseY: Int,
			hoverInfo: util.List[String]): Unit = {
		this.func_146283_a(hoverInfo, mouseX, mouseY)
	}

	override def drawString(string: String, x: Int, y: Int, color: Int): Unit =
		this.fontRendererObj.drawString(string, x, y, color)

	override def getStringWidth(string: String): Int = this.fontRendererObj.getStringWidth(string)

}

object GuiRadialMenu {

	abstract class RadialHandler[T <: IRadialSelection] {
		def getRadialFromGlobalID(globalID: Int): T

		final def onSelection(globalID: Int, player: EntityPlayer): Unit = {
			this.getRadialFromGlobalID(globalID).onSelection(player)

		}

	}

	private val radialHandlerMap = ListBuffer[RadialHandler[_]]()

	def register[T <: IRadialSelection](obj: RadialHandler[T],
			radialSelectionClass: Class[T]): Unit = {
		this.radialHandlerMap += obj
	}

	def getHandlerID[T <: IRadialSelection](radialHandler: RadialHandler[T]): Int = {
		this.radialHandlerMap.indexOf(radialHandler)
	}

	def onSelection(handlerID: Int, radialGlobalID: Int, player: EntityPlayer): Unit = {
		this.radialHandlerMap(handlerID).onSelection(radialGlobalID, player)
	}

}
