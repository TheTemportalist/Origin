package temportalist.origin.foundation.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{SideOnly, Side}
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiButtonImg(id: Int, name: String, x: Int, y: Int, w: Int, h: Int,
		private val u: Int, private val v: Int,
		private val uDis: Int, private val vDis: Int,
		private val uHov: Int, private val vHov: Int,
		private val texString: String)
		extends GuiButton(id, x, y, w, h, name) {

	def this(id: Int, name: String, x: Int, y: Int, w: Int, h: Int, u: Int, v: Int, tex: String) {
		this(id, name, x, y, w, h, u, v, u, v, u, v, tex)
	}

	private val tex: ResourceLocation = new ResourceLocation(texString)

	override def drawButton(mc: Minecraft, mX: Int, mY: Int): Unit = {
		mc.renderEngine.bindTexture(this.tex)
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
		GL11.glEnable(GL11.GL_BLEND)
		OpenGlHelper.glBlendFunc(770, 771, 1, 0)
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
		if (this.enabled) {
			if (this.intersectsWith(mX, mY))
				this.drawTexturedModalRect(this.xPosition, this.yPosition, this.uHov, this.vHov,
					this.width, this.height)
			else
				this.drawTexturedModalRect(this.xPosition, this.yPosition, this.u, this.v,
					this.width, this.height)
		}
		else
			this.drawTexturedModalRect(this.xPosition, this.yPosition, this.uDis, this.vDis,
				this.width, this.height)
	}

	def intersectsWith(mx: Int, my: Int): Boolean =
		mx >= this.xPosition && mx <= this.xPosition + this.width &&
				my >= this.yPosition && my <= this.yPosition + this.height

}
