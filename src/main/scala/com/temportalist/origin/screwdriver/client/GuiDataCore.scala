package com.temportalist.origin.screwdriver.client

import com.temportalist.origin.api.client.gui.GuiScreenBase
import com.temportalist.origin.api.client.utility.Rendering
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import com.temportalist.origin.screwdriver.common.behaviors.datacore.EntityState
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.{OpenGlHelper, RenderHelper, Tessellator}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.{GL11, GL12}

/**
 * Created by TheTemportalist on 12/25/2015.
 */
@SideOnly(Side.CLIENT)
class GuiDataCore(private val player: EntityPlayer) extends GuiScreenBase {

	this.setupGui("", null)
	this.setSize(185, 119)

	private val stack = player.getCurrentEquippedItem
	private var states: Array[EntityState] = null
	private var primaryColor: (Float, Float, Float) = null
	private var secondaryColor: (Float, Float, Float) = null
	private var stateListIndexOffset = 0

	private val pos_buttonTop = (31, 5)
	private val pos_buttonBottom = (31, 110)
	private val pos_buttonSize = (8, 4)
	private val pos_stateList = (5, 12)
	private val pos_size_stateList = (60, 11)
	private val pos_space_stateList = 1
	private val pos_x_stateList_text = 16
	private val pos_state_render = (75, 6)
	private val pos_state_name = (121, 23)
	private val pos_state_desc = (75, 40)
	private val pos_y_stateList_all = new Array[Int](8)
	private var selectedStateListIndex = 0

	override def initGui(): Unit = {
		super.initGui()

		this.states = AddonScrewdriver.NBTBehaviorHelper.getScannedEntityStates(this.stack)

		this.primaryColor = (31, 219, 49)
		this.secondaryColor = (165, 124, 46)

		for (i <- this.pos_y_stateList_all.indices)
			this.pos_y_stateList_all(i) = this.pos_stateList._2 +
					(this.pos_size_stateList._2 + this.pos_space_stateList) * i

	}

	override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
		super.mouseClicked(mouseX, mouseY, mouseButton)

		val left = this.getX()
		val top = this.getY()

		def isInButtonArea(xy: (Int, Int)): Boolean =
			this.isMouseInArea(xy._1 + left, xy._2 + top,
				this.pos_buttonSize._1, this.pos_buttonSize._2, mouseX, mouseY)
		if (this.states.length > 8) {
			if (isInButtonArea(this.pos_buttonTop)) {
				// decrement counter
				this.stateListIndexOffset -= 1
				if (this.stateListIndexOffset < 0)
					this.stateListIndexOffset += this.states.length
				println("Index Offset = " + this.stateListIndexOffset)
			}
			else if (isInButtonArea(this.pos_buttonBottom)) {
				// increment back
				this.stateListIndexOffset += 1
				if (this.stateListIndexOffset >= this.states.length)
					this.stateListIndexOffset -= this.states.length
				println("Index Offset = " + this.stateListIndexOffset)
			}
		}
		def isInStateIndex(y: Int): Boolean =
			this.isMouseInArea(this.pos_stateList._1 + left, y + top,
				this.pos_size_stateList._1, this.pos_size_stateList._2, mouseX, mouseY)
		for (i <- this.pos_y_stateList_all.indices) {
			if (isInStateIndex(this.pos_y_stateList_all(i))) {
				this.selectedStateListIndex = i + this.stateListIndexOffset
				if (this.selectedStateListIndex >= this.states.length)
					this.selectedStateListIndex -= this.states.length
				println("State index = " + this.selectedStateListIndex)
			}
		}
	}

	override protected def drawGuiBackground(): Unit = {
		for (i <- 0 to 3) {
			Rendering.Gl.push()
			var color = (1f, 1f, 1f)
			if (i <= 1) color = primaryColor
			else if (i == 2) color = secondaryColor
			//Rendering.Gl.color(color._1 / 255f, color._2 / 255F, color._3 / 255F)
			Rendering.bindResource(AddonScrewdriver.getResource("gui_datacore_" + i))
			this.drawTexturedModalRect(
				this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight())
			Rendering.Gl.pop()
		}
	}

	override protected def drawGuiBackgroundLayer(mouseX: Int, mouseY: Int,
			renderPartialTicks: Float): Unit = {
		super.drawGuiBackgroundLayer(mouseX, mouseY, renderPartialTicks)

		val left = this.getX()
		val top = this.getY()

		def renderEntityAndName(state: EntityState, renderXY: (Int, Int), renderMag: Float,
				textXY: (Int, Int)): Unit = {
			this.drawEntityOnScreen(state, state.getEntity,
				renderXY._1 + left, renderXY._2 + top, renderMag,
				2, 2, renderPartialTicks, false, false)
			this.drawString(state.getName, textXY._1 + left, textXY._2 + top)
		}

		for (i <- this.pos_y_stateList_all.indices) {
			var stateIndex = i + this.stateListIndexOffset
			if (this.states.length >= 8) {
				if (stateIndex >= this.states.length)
					stateIndex -= this.states.length
			}
			if (stateIndex >= 0 && stateIndex < this.states.length) {
				val state = this.states(stateIndex)
				val x = this.pos_stateList._1
				val y = this.pos_y_stateList_all(i)
				val entitySize = this.pos_size_stateList._2
				val x_entity = x + entitySize / 2
				val y_entity = y + entitySize
				val entSize =
					if (state.getEntity.width > state.getEntity.height)
						state.getEntity.width
					else state.getEntity.height
				val scaleMag = 2.5F / entSize
				val scale2 = if (entSize < 1.8F) 1.8F / entSize else 1
				renderEntityAndName(state, (x_entity, (y_entity * 0.99).toInt),
					(if (entSize > 2.5F) 16F * scaleMag else 16F) * 0.25f * scale2,
					((this.pos_x_stateList_text * 1.1f).toInt, y + entitySize / 4))
			}
		}

		val stateIndex = this.selectedStateListIndex
		if (stateIndex >= 0 && stateIndex < this.states.length) {
			val state = this.states(stateIndex)
			val entSize =
				if (state.getEntity.width > state.getEntity.height)
					state.getEntity.width
				else state.getEntity.height
			val scaleMag = 2.5F / entSize
			renderEntityAndName(this.states(stateIndex),
				(this.pos_state_render._1 + 21, this.pos_state_render._2 + 38),
				if (entSize > 2.5F) 16F * scaleMag else 16F, this.pos_state_name)
			this.drawString(state.getDescription, this.pos_state_desc._1, this.pos_state_desc._2)
		}

	}

	private def drawEntityOnScreen(state: EntityState, ent: EntityLivingBase,
			posX: Int, posY: Int, scale: Float, par4: Float, par5: Float,
			renderTick: Float, selected: Boolean, text: Boolean): Unit = {
		var forceRender = true
		if (ent != null) {
			val hideGui = Minecraft.getMinecraft.gameSettings.hideGUI

			Minecraft.getMinecraft.gameSettings.hideGUI = true

			GL11.glEnable(GL11.GL_COLOR_MATERIAL)
			GL11.glPushMatrix()

			GL11.glDisable(GL11.GL_ALPHA_TEST)

			GL11.glTranslatef(posX, posY, 50.0F)

			GL11.glScalef(-scale, scale, scale)
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F)
			val f2 = ent.renderYawOffset
			val f3 = ent.rotationYaw
			val f4 = ent.rotationPitch
			val f5 = ent.rotationYawHead

			GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F)
			RenderHelper.enableStandardItemLighting()
			GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F)
			GL11.glRotatef(-Math.atan((par5 / 40.0F).toDouble).toFloat * 20.0F, 1.0F, 0.0F, 0.0F)
			GL11.glRotatef(15.0F, 1.0F, 0.0F, 0.0F)
			GL11.glRotatef(25.0F, 0.0F, 1.0F, 0.0F)

			ent.renderYawOffset = Math.atan((par4 / 40.0F).toDouble).toFloat * 20.0F
			ent.rotationYaw = Math.atan((par4 / 40.0F).toDouble).toFloat * 40.0F
			ent.rotationPitch = -Math.atan((par5 / 40.0F).toDouble).toFloat * 20.0F
			ent.rotationYawHead = ent.renderYawOffset
			GL11.glTranslatef(0.0F, ent.yOffset, 0.0F)

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)

			if (ent.isInstanceOf[EntityDragon]) GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F)

			val viewY = RenderManager.instance.playerViewY
			RenderManager.instance.playerViewY = 180.0F
			RenderManager.instance.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F)

			if (ent.isInstanceOf[EntityDragon]) GL11.glRotatef(180F, 0.0F, -1.0F, 0.0F)

			GL11.glTranslatef(0.0F, -0.22F, 0.0F)
			OpenGlHelper.setLightmapTextureCoords(
				OpenGlHelper.lightmapTexUnit, 255.0F * 0.8F, 255.0F * 0.8F)
			Tessellator.instance.setBrightness(240)

			RenderManager.instance.playerViewY = viewY
			ent.renderYawOffset = f2
			ent.rotationYaw = f3
			ent.rotationPitch = f4
			ent.rotationYawHead = f5

			GL11.glPopMatrix()

			RenderHelper.disableStandardItemLighting()

			GL11.glPushMatrix()

			GL11.glTranslatef(posX, posY, 50.0F)

			GL11.glEnable(GL11.GL_BLEND)
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

			//MorphInfoClient info = playerMorphInfo.get(Minecraft.getMinecraft().thePlayer.getCommandSenderName());

			GL11.glTranslatef(0.0F, 0.0F, 100F)
			/*
			if(text)
			{
				if(radialShow)
				{
					GL11.glPushMatrix();
					float scaleee = 0.75F;
					GL11.glScalef(scaleee, scaleee, scaleee);
					String name = (selected ? EnumChatFormatting.YELLOW : (info != null && info.nextState.identifier.equalsIgnoreCase(state.identifier) || info == null && state.playerMorph.equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getCommandSenderName())) ? EnumChatFormatting.GOLD : "") + ent.getCommandSenderName();
					Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(name, (int)(-3 - (Minecraft.getMinecraft().fontRenderer.getStringWidth(name) / 2) * scaleee), 5, 16777215);
					GL11.glPopMatrix();
				}
				else
				{
					Minecraft.getMinecraft().fontRenderer.drawStringWithShadow((selected ? EnumChatFormatting.YELLOW : (info != null && info.nextState.entInstance.getCommandSenderName().equalsIgnoreCase(state.entInstance.getCommandSenderName()) || info == null && ent.getCommandSenderName().equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getCommandSenderName())) ? EnumChatFormatting.GOLD : "") + ent.getCommandSenderName(), 26, -32, 16777215);
				}

				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
			*/

			/*
			if(state != null && !state.playerMorph.equalsIgnoreCase(state.playerName) && state.isFavourite)
			{
				double pX = 9.5D;
				double pY = -33.5D;
				double size = 9D;

				Minecraft.getMinecraft().getTextureManager().bindTexture(rlFavourite);

				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				Tessellator tessellator = Tessellator.instance;
				tessellator.setColorRGBA(255, 255, 255, 255);

				tessellator.startDrawingQuads();
				double iconX = pX;
				double iconY = pY;

				tessellator.addVertexWithUV(iconX, iconY + size, 0.0D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(iconX + size, iconY + size, 0.0D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(iconX + size, iconY, 0.0D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(iconX, iconY, 0.0D, 0.0D, 0.0D);
				tessellator.draw();

				GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.6F);

				tessellator.startDrawingQuads();
				iconX = pX + 1D;
				iconY = pY + 1D;

				tessellator.addVertexWithUV(iconX, iconY + size, -1.0D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(iconX + size, iconY + size, -1.0D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(iconX + size, iconY, -1.0D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(iconX, iconY, -1.0D, 0.0D, 0.0D);
				tessellator.draw();
			}
			*/

			/*
			if(Morph.config.getSessionInt("showAbilitiesInGui") == 1)
			{
				ArrayList<Ability> abilities = AbilityHandler.getEntityAbilities(ent.getClass());

				int abilitiesSize = abilities.size();
				for(int i = abilities.size() - 1; i >= 0; i--)
				{
					if(!abilities.get(i).entityHasAbility(ent) || (abilities.get(i).getIcon() == null && !(abilities.get(i) instanceof AbilityPotionEffect)) || abilities.get(i) instanceof AbilityPotionEffect && Potion.potionTypes[((AbilityPotionEffect)abilities.get(i)).potionId] != null && !Potion.potionTypes[((AbilityPotionEffect)abilities.get(i)).potionId].hasStatusIcon())
					{
						abilitiesSize--;
					}
				}

				boolean shouldScroll = false;

				final int stencilBit = MinecraftForgeClient.reserveStencilBit();

				if(stencilBit >= 0 && abilitiesSize > 3)
				{
					MorphState selectedState = null;

					int i = 0;

					Iterator<Entry<String, ArrayList<MorphState>>> ite = playerMorphCatMap.entrySet().iterator();

					while(ite.hasNext())
					{
						Entry<String, ArrayList<MorphState>> e = ite.next();
						if(i == selectorSelected)
						{
							ArrayList<MorphState> states = e.getValue();

							for(int j = 0; j < states.size(); j++)
							{
								if(j == selectorSelectedHori)
								{
									selectedState = states.get(j);
									break;
								}
							}

							break;
						}
						i++;
					}

					if(state != null && selectedState == state)
					{
						shouldScroll = true;
					}

					if(shouldScroll)
					{
						final int stencilMask = 1 << stencilBit;

						GL11.glEnable(GL11.GL_STENCIL_TEST);
						GL11.glDepthMask(false);
						GL11.glColorMask(false, false, false, false);

						GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
						GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);  // draw 1s on test fail (always)
						GL11.glStencilMask(stencilMask);
						GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

						RendererHelper.drawColourOnScreen(255, 255, 255, 255, -20.5D, -32.5D, 40D, 35D, -10D);

						GL11.glStencilMask(0x00);
						GL11.glStencilFunc(GL11.GL_EQUAL, stencilMask, stencilMask);

						GL11.glDepthMask(true);
						GL11.glColorMask(true, true, true, true);
					}
				}

				int offsetX = 0;
				int offsetY = 0;
				int renders = 0;
				for(int i = 0; i < (abilitiesSize > 3 && stencilBit >= 0 && abilities.size() > 3 ? abilities.size() * 2 : abilities.size()); i++)
				{
					Ability ability = abilities.get(i >= abilities.size() ? i - abilities.size() : i);

					if(!ability.entityHasAbility(ent) || (ability.getIcon() == null && !(ability instanceof AbilityPotionEffect)) || ability instanceof AbilityPotionEffect && Potion.potionTypes[((AbilityPotionEffect)ability).potionId] != null && !Potion.potionTypes[((AbilityPotionEffect)ability).potionId].hasStatusIcon() || (abilitiesSize > 3 && stencilBit >= 0 && abilities.size() > 3) && !shouldScroll && renders >= 3)
					{
						continue;
					}

					ResourceLocation loc = ability.getIcon();
					if(loc != null || ability instanceof AbilityPotionEffect)
					{
						double pX = -20.5D;
						double pY = -33.5D;
						double size = 12D;

						if(stencilBit >= 0 && abilities.size() > 3 && shouldScroll)
						{
							int round = abilityScroll % (30 * abilities.size());

							pY -= (size + 1) * (double)(round + (double)renderTick) / 30D;
						}

						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						Tessellator tessellator = Tessellator.instance;
						tessellator.setColorRGBA(255, 255, 255, 255);

						double iconX = pX + (offsetX * (size + 1));
						double iconY = pY + (offsetY * (size + 1));

						if(loc != null)
						{
							Minecraft.getMinecraft().getTextureManager().bindTexture(loc);

							tessellator.startDrawingQuads();
							tessellator.addVertexWithUV(iconX, iconY + size, 0.0D, 0.0D, 1.0D);
							tessellator.addVertexWithUV(iconX + size, iconY + size, 0.0D, 1.0D, 1.0D);
							tessellator.addVertexWithUV(iconX + size, iconY, 0.0D, 1.0D, 0.0D);
							tessellator.addVertexWithUV(iconX, iconY, 0.0D, 0.0D, 0.0D);
							tessellator.draw();
						}
						else
						{
							Minecraft.getMinecraft().getTextureManager().bindTexture(rlGuiInventory);
							int l = Potion.potionTypes[((AbilityPotionEffect)ability).potionId].getStatusIconIndex();

							float f = 0.00390625F;
							float f1 = 0.00390625F;

							int xStart = l % 8 * 18;
							int yStart = 198 + l / 8 * 18;

							tessellator.startDrawingQuads();
							tessellator.addVertexWithUV(iconX, iconY + size, 0.0D, xStart * f, (yStart + 18) * f1);
							tessellator.addVertexWithUV(iconX + size, iconY + size, 0.0D, (xStart + 18) * f, (yStart + 18) * f1);
							tessellator.addVertexWithUV(iconX + size, iconY, 0.0D, (xStart + 18) * f, yStart * f1);
							tessellator.addVertexWithUV(iconX, iconY, 0.0D, xStart * f, yStart * f1);
							tessellator.draw();

						}

						GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.6F);

						size = 12D;
						iconX = pX + 1D + (offsetX * (size + 1));
						iconY = pY + 1D + (offsetY * (size + 1));

						if(loc != null)
						{
							tessellator.startDrawingQuads();
							tessellator.addVertexWithUV(iconX, iconY + size, -1.0D, 0.0D, 1.0D);
							tessellator.addVertexWithUV(iconX + size, iconY + size, -1.0D, 1.0D, 1.0D);
							tessellator.addVertexWithUV(iconX + size, iconY, -1.0D, 1.0D, 0.0D);
							tessellator.addVertexWithUV(iconX, iconY, -1.0D, 0.0D, 0.0D);
							tessellator.draw();
						}
						else
						{
							Minecraft.getMinecraft().getTextureManager().bindTexture(rlGuiInventory);
							int l = Potion.potionTypes[((AbilityPotionEffect)ability).potionId].getStatusIconIndex();

							float f = 0.00390625F;
							float f1 = 0.00390625F;

							int xStart = l % 8 * 18;
							int yStart = 198 + l / 8 * 18;

							tessellator.startDrawingQuads();
							tessellator.addVertexWithUV(iconX, iconY + size, -1.0D, xStart * f, (yStart + 18) * f1);
							tessellator.addVertexWithUV(iconX + size, iconY + size, -1.0D, (xStart + 18) * f, (yStart + 18) * f1);
							tessellator.addVertexWithUV(iconX + size, iconY, -1.0D, (xStart + 18) * f, yStart * f1);
							tessellator.addVertexWithUV(iconX, iconY, -1.0D, xStart * f, yStart * f1);
							tessellator.draw();
						}

						offsetY++;
						if(offsetY == 3 && stencilBit < 0)
						{
							offsetY = 0;
							offsetX++;
						}
					}
					renders++;
				}

				if(stencilBit >= 0 && abilities.size() > 3 && shouldScroll)
				{
					GL11.glDisable(GL11.GL_STENCIL_TEST);
				}

				MinecraftForgeClient.releaseStencilBit(stencilBit);
			}
			*/
			GL11.glTranslatef(0.0F, 0.0F, -100F)

			GL11.glDisable(GL11.GL_BLEND)

			GL11.glPopMatrix()

			GL11.glEnable(GL11.GL_ALPHA_TEST)

			GL11.glDisable(GL12.GL_RESCALE_NORMAL)
			OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit)
			GL11.glDisable(GL11.GL_TEXTURE_2D)
			OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit)

			Minecraft.getMinecraft.gameSettings.hideGUI = hideGui
		}
		forceRender = false
	}

}
