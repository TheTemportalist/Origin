package com.temportalist.origin.internal.client.gui

import com.temportalist.origin.api.client.utility.Rendering
import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.api.common.utility.MathFuncs
import com.temportalist.origin.internal.common.{CGOOptions, Origin}
import cpw.mods.fml.common.Loader
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.{GuiIngame, ScaledResolution}
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.potion.Potion
import net.minecraft.util.{MathHelper, ResourceLocation}
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import org.lwjgl.opengl.GL11
import org.lwjgl.util.Color

/**
 *
 *
 * @author TheTemportalist
 */
object HealthOverlay extends GuiIngame(Rendering.mc) {

	val halfOffset: V3O = new V3O(0, 9)
	val overlayOffset: V3O = new V3O(9, 0)
	val highlightOffset: V3O = new V3O(18, 0)

	val background: V3O = new V3O(0, 0)
	val base: V3O = new V3O(0, 18)
	val poison: V3O = new V3O(0, 36)
	val wither: V3O = new V3O(0, 54)

	val heartSide: Int = 9

	@SubscribeEvent
	def heartOverlay(event: RenderGameOverlayEvent.Pre): Unit = {
		// take care of possible conflicting mods
		val ticon_Valid: Boolean = !Loader.isModLoaded("TConstruct")
		val rpghud_Valid: Boolean = !Loader.isModLoaded("rpghud")
		val tukmc_Vz_borderlands_Valid: Boolean =
			!Loader.isModLoaded("tukmc_Vz") || Loader.isModLoaded("borderlands")
		if (!ticon_Valid || !rpghud_Valid || !tukmc_Vz_borderlands_Valid) return
		if (event.`type` != ElementType.HEALTH) return

		// calculate various variables for display
		val reso: ScaledResolution = Rendering.getScaledResoultion
		val width: Int = reso.getScaledWidth
		val height: Int = reso.getScaledHeight

		val player: EntityPlayerSP = Rendering.mc.thePlayer
		val absorb = player.getAbsorptionAmount.toInt

		val xHearts: Int = width / 2 - 91
		val yHearts: Int = height - GuiIngameForge.left_height
		GuiIngameForge.left_height += 10

		val hurtResistantTime: Int = player.hurtResistantTime
		val highlight: Boolean = hurtResistantTime >= 10 || hurtResistantTime / 3 % 2 == 1

		val health: Int = MathHelper.ceiling_float_int(player.getHealth)
		val tier: Int = (health - 1) / 20

		GL11.glEnable(GL11.GL_BLEND)
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
		if (player.getActivePotionEffect(Potion.wither) != null)
			this.drawHearts(-1, healthToDraw, xHearts, yHearts, highlight, this.wither)
		if (player.getActivePotionEffect(Potion.poison) != null)
			this.drawHearts(-1, healthToDraw, xHearts, yHearts, highlight, this.poison)
		else
			this.drawHearts(tier, healthToDraw, xHearts, yHearts, highlight, this.base)
		if (absorb > 0) {
			this.drawHearts(tier + 1, absorb, xHearts, yHearts, highlight, this.base)
		}

		GL11.glDisable(GL11.GL_BLEND)

		Rendering.bindResource(this.icons)
		event.setCanceled(true)
	}

	def r(color: Color): Float = color.getRed / 255f

	def g(color: Color): Float = color.getGreen / 255f

	def b(color: Color): Float = color.getBlue / 255f

	def drawHearts(tier: Int, health: Int, xBase: Int, yBase: Int,
			highlight: Boolean, baseLayerUV: V3O
			): Unit = {
		val fullHearts: Int = health / 2
		val halfHeart: Boolean = health % 2 > 0

		// base layer
		if (tier >= 0) {
			val color: Color = MathFuncs.getColor("#", CGOOptions.heartColors(
				if (tier < CGOOptions.heartColors.length) tier else
					this.rand.nextInt(CGOOptions.heartColors.length)
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
			uv: V3O): Unit = {
		for (full_heart <- 0 to fullHearts - 1) {
			this.drawHeart(xBase + (full_heart * 8), yBase, uv)
		}
		if (halfHeart) this.drawHeart(xBase + (fullHearts * 8), yBase, uv + this.halfOffset)
	}

	def drawHeart(x: Int, y: Int, uv: V3O): Unit = {
		this.drawTexturedModalRect(x, y, uv.u(), uv.v(), this.heartSide, this.heartSide)
	}

	val hearts: ResourceLocation = new ResourceLocation(Origin.MODID, "textures/gui/hearts.png")
	val icons: ResourceLocation = new ResourceLocation("textures/gui/icons.png")

}
