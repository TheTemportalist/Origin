package temportalist.origin.internal.client

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent.{ElementType, Pre}
import net.minecraftforge.fml.common.Loader
import org.lwjgl.opengl.GL11
import org.lwjgl.util.Color
import temportalist.origin.api.client.Rendering
import temportalist.origin.api.common.lib.Vect
import temportalist.origin.api.common.utility.MathFuncs
import temportalist.origin.foundation.client.gui.IOverlay
import temportalist.origin.internal.common.{Options, Origin}

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
object OverlayHealth extends IOverlay {

	val halfOffset = new Vect(0, 9)
	val overlayOffset = new Vect(9, 0)
	val highlightOffset = new Vect(18, 0)

	val background = new Vect(0, 0)
	val base = new Vect(0, 18)
	val poison = new Vect(0, 36)
	val wither = new Vect(0, 54)

	val heartSide: Int = 9

	override def pre(event: Pre): Unit = {
		val ticon_Valid: Boolean = !Loader.isModLoaded("TConstruct")
		val rpghud_Valid: Boolean = !Loader.isModLoaded("rpghud")
		val tukmc_Vz_borderlands_Valid: Boolean =
			!Loader.isModLoaded("tukmc_Vz") || Loader.isModLoaded("borderlands")
		if (!ticon_Valid || !rpghud_Valid || !tukmc_Vz_borderlands_Valid) return
		if (event.getType != ElementType.HEALTH) return

		val reso = event.getResolution
		val width = reso.getScaledWidth
		val height = reso.getScaledHeight

		val player: EntityPlayerSP = Minecraft.getMinecraft.thePlayer
		val absorb = player.getAbsorptionAmount.toInt

		val xHearts: Int = width / 2 - 91
		val yHearts: Int = height - GuiIngameForge.left_height
		GuiIngameForge.left_height += 10

		val hurtResistantTime: Int = player.hurtResistantTime
		val highlight: Boolean = hurtResistantTime >= 10 || hurtResistantTime / 3 % 2 == 1

		val health: Int = MathHelper.ceiling_float_int(player.getHealth)
		val tier: Int = (health - 1) / 20

		GlStateManager.enableBlend()
		Rendering.bindResource(this.hearts)
		if (tier <= 0) {
			// render background
			// only render the base background if we are going to see it
			this.drawHearts(-1, 20, xHearts, yHearts, highlight, this.background)
		}
		else {
			// render background
			// if not going to see base background, only render the edge part
			this.drawHeartsLayer(
				xHearts, yHearts, 10, halfHeart = false,
				this.background + (if (highlight) this.highlightOffset else this.overlayOffset)
			)

			// render the hearts of the tier before
			this.drawHearts(tier - 1, 20, xHearts, yHearts, highlight, this.base)
		}
		// patch the actually health we are drawing
		var healthToDraw: Int = health % 20
		if (healthToDraw == 0 && health > 0) healthToDraw = 20
		// draw the hearts of this tier
		// if we are poisoned or withered, render those instead
		if (player.getActivePotionEffect(Potion.getPotionFromResourceLocation("wither")) != null)
			this.drawHearts(-1, healthToDraw, xHearts, yHearts, highlight, this.wither)
		if (player.getActivePotionEffect(Potion.getPotionFromResourceLocation("poison")) != null)
			this.drawHearts(-1, healthToDraw, xHearts, yHearts, highlight, this.poison)
		else
			this.drawHearts(tier, healthToDraw, xHearts, yHearts, highlight, this.base)
		if (absorb > 0) {
			this.drawHearts(tier + 1, absorb, xHearts, yHearts, highlight, this.base)
		}

		GlStateManager.disableBlend()

		Rendering.bindResource(this.icons)
		event.setCanceled(true)
	}

	def r(color: Color): Float = color.getRed / 255f

	def g(color: Color): Float = color.getGreen / 255f

	def b(color: Color): Float = color.getBlue / 255f

	def drawHearts(tier: Int, health: Int, xBase: Int, yBase: Int,
			highlight: Boolean, baseLayerUV: Vect
	): Unit = {
		val fullHearts: Int = health / 2
		val halfHeart: Boolean = health % 2 > 0

		// base layer
		if (tier >= 0) {
			val color: Color = MathFuncs.getColor("#", Options.heartColors(
				if (tier < Options.heartColors.length) tier
				else Minecraft.getMinecraft.thePlayer.getRNG.nextInt(Options.heartColors.length)
			))
			GL11.glColor3f(this.r(color), this.g(color), this.b(color))
		}
		this.drawHeartsLayer(xBase, yBase, fullHearts, halfHeart, baseLayerUV)

		// overlay/highlight layer
		GL11.glColor3f(1f, 1f, 1f)
		this.drawHeartsLayer(
			xBase, yBase, fullHearts, halfHeart,
			baseLayerUV + (if (highlight) this.highlightOffset else this.overlayOffset)
		)

	}

	def drawHeartsLayer(xBase: Int, yBase: Int, fullHearts: Int, halfHeart: Boolean,
			uv: Vect): Unit = {
		for (full_heart <- 0 until fullHearts) this.drawHeart(xBase + (full_heart * 8), yBase, uv)
		if (halfHeart) this.drawHeart(xBase + (fullHearts * 8), yBase, uv + this.halfOffset)
	}

	def drawHeart(x: Int, y: Int, uv: Vect): Unit = {
		Rendering.drawTexture((x, y), (uv.x_i(), uv.y_i()), (this.heartSide, this.heartSide))
	}

	val hearts = new ResourceLocation(Origin.getModId, "textures/gui/hearts.png")
	val icons = new ResourceLocation("textures/gui/icons.png")

}
