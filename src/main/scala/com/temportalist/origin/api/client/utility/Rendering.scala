package com.temportalist.origin.api.client.utility

import java.io.IOException

import cpw.mods.fml.client.registry.{ClientRegistry, RenderingRegistry}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.{Gui, GuiScreen, ScaledResolution}
import net.minecraft.client.renderer.{RenderBlocks, OpenGlHelper}
import net.minecraft.client.renderer.entity.{RenderItem, RenderEntity, RenderManager}
import net.minecraft.client.renderer.texture.{TextureUtil, SimpleTexture, TextureAtlasSprite}
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.{IItemRenderer, MinecraftForgeClient}
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
object Rendering {

	def mc: Minecraft = Minecraft.getMinecraft

	def renderManager: RenderManager = RenderManager.instance

	def thePlayer = this.mc.thePlayer

	private val renderBlocks = new RenderBlocks

	def getRenderBlocks: RenderBlocks = this.renderBlocks

	private val renderItem = new RenderItem
	this.renderItem.setRenderManager(RenderManager.instance)

	def getRenderItem: RenderItem = this.renderItem

	//def blockDispatcher: BlockRendererDispatcher = this.mc.getBlockRendererDispatcher

	//def blockShapes: BlockModelShapes = this.blockDispatcher.getBlockModelShapes

	//def renderItem: RenderItem = this.mc.rende

	//def itemMesher: ItemModelMesher = this.renderItem.getItemModelMesher

	def display(gui: GuiScreen): Unit = this.mc.displayGuiScreen(gui)

	def bindResource(rl: ResourceLocation): Unit = {
		Rendering.mc.getTextureManager.bindTexture(rl)
	}

	/**
	 * Will draw the currently bound resource using parameters
	 * @param pos The position on the screen
	 * @param uv The XY value in the texture (upper-left corner)
	 * @param actualSize The width and height of the image inside the texture
	 * @param renderedSize The width and height to be scaled to for rendering
	 * @param imgSize The size of the resource location in Width by Height
	 * @param offsets The offsets to draw the image. Each offset represents
	 *                X pixels from that side to the center. LEft by Right by Top by Bottom
	 */
	def drawTextureScaledWithOffsets(pos: (Int, Int), uv: (Float, Float), actualSize: (Int, Int),
			renderedSize: (Int, Int), imgSize: (Float, Float),
			offsets: (Int, Int, Int, Int)): Unit = {
		/*
		Rendering.drawTextureRect(
			x + leftOffset,
			y + topOffset,
			u + leftOffset,
			v + topOffset,
			w - rightOffset - leftOffset,
			h - bottomOffset - topOffset
		)
		*/
		val renderToActual: Float = renderedSize._1.toFloat / actualSize._1

		val offsetLeft_A: Int = (offsets._1 * renderToActual).toInt
		val offsetRight_A: Int = (offsets._2 * renderToActual).toInt
		val offsetTop_A: Int = (offsets._3 * renderToActual).toInt
		val offsetBottom_A: Int = (offsets._4 * renderToActual).toInt

		Rendering.drawTextureWithSizes(
			(pos._1 + offsetLeft_A, pos._2 + offsetTop_A),
			(uv._1 + offsetLeft_A, uv._2 + offsetTop_A),
			(actualSize._1 - offsetLeft_A - offsetRight_A,
					actualSize._2 - offsetTop_A - offsetBottom_A),
			(renderedSize._1 - offsets._1 - offsets._2, renderedSize._2 - offsets._3 - offsets._4),
			imgSize
		)
	}

	def drawTextureWithSizes(pos: (Int, Int), uv: (Float, Float), actualSize: (Int, Int),
			renderedSize: (Int, Int), imgSize: (Float, Float)): Unit = {
		Gui.func_152125_a(pos._1, pos._2, uv._1, uv._2, actualSize._1, actualSize._2,
			renderedSize._1, renderedSize._2, imgSize._1, imgSize._2)
	}

	def drawTexture(pos: (Int, Int), uv: (Float, Float), size: (Int, Int), imgSize: (Float, Float)): Unit = {
		this.drawTextureWithSizes(pos, uv, size, size, imgSize)
	}

	def drawTexture(pos: (Int, Int), uv: (Float, Float), size: (Int, Int)): Unit =
		this.drawTexture(pos, uv, size, (256, 256))

	def getSprite(iconName: String): TextureAtlasSprite =
		Rendering.mc.getTextureMapBlocks.getAtlasSprite(iconName)

	def drawSprite(x: Double, y: Double, z: Double, location: ResourceLocation, w: Double,
			h: Double): Unit = {
		this.drawSprite(x, y, z, this.getSprite(location.toString), w, h)
	}

	def drawSprite(x: Double, y: Double, z: Double, sprite: TextureAtlasSprite, w: Double,
			h: Double): Unit = {
		TessRenderer.startQuads()
		TessRenderer.addVertex(
			x + 0,
			y + h,
			z,
			sprite.getMinU.asInstanceOf[Double],
			sprite.getMaxV.asInstanceOf[Double]
		)
		TessRenderer.addVertex(
			x + w,
			y + h,
			z,
			sprite.getMaxU.asInstanceOf[Double],
			sprite.getMaxV.asInstanceOf[Double]
		)
		TessRenderer.addVertex(
			x + w,
			y + 0,
			z,
			sprite.getMaxU.asInstanceOf[Double],
			sprite.getMinV.asInstanceOf[Double]
		)
		TessRenderer.addVertex(
			x + 0,
			y + 0,
			z,
			sprite.getMinU.asInstanceOf[Double],
			sprite.getMinV.asInstanceOf[Double]
		)
		TessRenderer.draw()
	}

	def modelCoordsToVerticies(x: Float, y: Float, z: Float, color: Int,
			texture: TextureAtlasSprite, u: Float, v: Float): Array[Int] = {
		Array[Int](
			java.lang.Float.floatToRawIntBits(x),
			java.lang.Float.floatToRawIntBits(y),
			java.lang.Float.floatToRawIntBits(z),
			color,
			java.lang.Float.floatToRawIntBits(texture.getInterpolatedU(u)),
			java.lang.Float.floatToRawIntBits(texture.getInterpolatedV(v)),
			0
		)
	}

	/*
	def getModel(stack: ItemStack, isItem: Boolean): IBakedModel = {
		if (!isItem && WorldHelper.isBlock(stack.getItem))
			Rendering.blockShapes.getModelForState(States.getState(stack))
		else
			Rendering.itemMesher.getItemModel(stack)
	}
	*/

	def getScaledResoultion: ScaledResolution =
		new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight)

	def registerRender(entity: Class[_ <: Entity], renderer: RenderEntity): Unit =
		RenderingRegistry.registerEntityRenderingHandler(entity, renderer)

	def registerRender(tile: Class[_ <: TileEntity], renderer: TileEntitySpecialRenderer): Unit =
		ClientRegistry.bindTileEntitySpecialRenderer(tile, renderer)

	def registerRender(item: Item, renderer: IItemRenderer): Unit =
		MinecraftForgeClient.registerItemRenderer(item, renderer)

	def registerRender(block: Block, renderer: IItemRenderer): Unit =
		this.registerRender(Item.getItemFromBlock(block), renderer)

	def doesTextureExist(resourceLocation: ResourceLocation): Boolean = {
		val textureManager = Rendering.mc.getTextureManager
		var textureObject = textureManager.getTexture(resourceLocation)
		if (textureObject == null) {
			textureObject = new SimpleTexture(resourceLocation)
			try textureObject.loadTexture(Rendering.mc.getResourceManager)
			catch {
				case exception: IOException => return false
				// no other cases because nothing else should be thrown
			}
			return true
		}
		textureObject != TextureUtil.missingTexture
	}

	object Gl {

		def push() = GL11.glPushMatrix()

		def pop() = GL11.glPopMatrix()

		def color(r: Float, g: Float, b: Float): Unit = GL11.glColor3f(r, g, b)

		def color(r: Double, g: Double, b: Double): Unit = GL11.glColor3d(r, g, b)

		def color(r: Float, g: Float, b: Float, a: Float): Unit = GL11.glColor4f(r, g, b, a)

		def color(r: Double, g: Double, b: Double, a: Double): Unit = GL11.glColor4d(r, g, b, a)

		def colorFull(): Unit = this.color(1, 1, 1, 1)

		def enable(i: Int, isOn: Boolean): Unit = if (isOn) GL11.glEnable(i) else GL11.glDisable(i)

		def blend(isOn: Boolean): Unit = this.enable(GL11.GL_BLEND, isOn)

		def blendFunc(a: Int, b: Int, c: Int, d: Int): Unit = OpenGlHelper.glBlendFunc(a, b, c, d)

		def blendFunc(typeA: Int, typeB: Int): Unit = GL11.glBlendFunc(typeA, typeB)

		def blendSrcAlpha(): Unit = this.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

		def pushAttribute(attr: Int): Unit = {
			GL11.glPushAttrib(attr)
		}

		def popAttribute(): Unit = {
			GL11.glPopAttrib()
		}

	}

}
