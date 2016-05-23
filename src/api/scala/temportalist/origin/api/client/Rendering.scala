package temportalist.origin.api.client

import java.io.IOException

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.{Gui, GuiScreen, ScaledResolution}
import net.minecraft.client.renderer._
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.{SimpleTexture, TextureAtlasSprite, TextureManager, TextureUtil}
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

/**
 * Provides and easy-to-access for objects and functions in rendering code
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
object Rendering {

	def mc: Minecraft = Minecraft.getMinecraft

	def thePlayer = this.mc.thePlayer

	def renderManager: RenderManager = this.mc.getRenderManager

	def getTextureManager: TextureManager = this.mc.renderEngine

	def blockDispatcher: BlockRendererDispatcher = this.mc.getBlockRendererDispatcher

	def blockShapes: BlockModelShapes = this.blockDispatcher.getBlockModelShapes

	def renderItem: RenderItem = this.mc.getRenderItem

	def itemMesher: ItemModelMesher = this.renderItem.getItemModelMesher

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
		Gui.drawScaledCustomSizeModalRect(pos._1, pos._2, uv._1, uv._2, actualSize._1, actualSize._2,
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
		TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX)
		TessRenderer.getBuffer.pos(x + 0, y + h, z).tex(sprite.getMinU, sprite.getMaxV).endVertex()
		TessRenderer.getBuffer.pos(x + w, y + h, z).tex(sprite.getMaxU, sprite.getMaxV).endVertex()
		TessRenderer.getBuffer.pos(x + w, y + 0, z).tex(sprite.getMaxU, sprite.getMinV).endVertex()
		TessRenderer.getBuffer.pos(x + 0, y + 0, z).tex(sprite.getMinU, sprite.getMinV).endVertex()
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

	def getScaledResoultion: ScaledResolution = new ScaledResolution(this.mc)

	/*
	def registerRender(entity: Class[_ <: Entity], renderer: RenderEntity): Unit =
		RenderingRegistry.registerEntityRenderingHandler(entity, renderer)

	def registerRender(tile: Class[_ <: TileEntity], renderer: TileEntitySpecialRenderer): Unit =
		ClientRegistry.bindTileEntitySpecialRenderer(tile, renderer)

	def registerRender(item: Item, renderer: IItemRenderer): Unit =
		MinecraftForgeClient.registerItemRenderer(item, renderer)

	def registerRender(block: Block, renderer: IItemRenderer): Unit =
		this.registerRender(Item.getItemFromBlock(block), renderer)
	*/

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
		textureObject != TextureUtil.MISSING_TEXTURE
	}

	def push_gl() = GlStateManager.pushMatrix()

	def pop_gl() = GlStateManager.popMatrix()

	def color_gl(rgb: (Float, Float, Float)): Unit = this.color_gl(rgb._1, rgb._2, rgb._3)

	def color_gl(rgba: (Float, Float, Float, Float)): Unit =
		this.color_gl(rgba._1, rgba._2, rgba._3, rgba._4)
	
	def color_gl(r: Float, g: Float, b: Float, a: Float = 1F): Unit = GlStateManager.color(r, g, b, a)

	def colorFull_gl(): Unit = this.color_gl(1, 1, 1)

	def alphaToggle(isOn: Boolean): Unit = {
		if (isOn) GlStateManager.enableAlpha()
		else GlStateManager.disableAlpha()
	}

	def blendToggle(isOn: Boolean): Unit = {
		if (isOn) GlStateManager.enableBlend()
		else GlStateManager.disableBlend()
	}

	def blendFunc(typeA: Int, typeB: Int): Unit = GlStateManager.blendFunc(typeA, typeB)

	def blendSrcAlpha(): Unit = this.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

	def translate_gl(x: Double, y: Double, z: Double): Unit =
		GlStateManager.translate(x, y, z)

}
