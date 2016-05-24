package temportalist.origin.api.client.gui

import java.util

import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.{GuiButton, GuiScreen, GuiTextField}
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.C17PacketCustomPayload
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{SideOnly, Side}
import org.apache.logging.log4j.LogManager
import org.lwjgl.input.{Keyboard, Mouse}
import org.lwjgl.opengl.GL11
import temportalist.origin.api.client.utility.Rendering

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
trait IGuiScreen extends GuiScreen {

	protected val defaultTextColor: Int = 4210752

	/** Starting X position for the Gui. Inconsistent use for Gui backgrounds. */
	protected var guiX: Int = 0
	/** Starting Y position for the Gui. Inconsistent use for Gui backgrounds. */
	protected var guiY: Int = 0
	/** The X size of the gui window in pixels. */
	private var guiW: Int = 176
	/** The Y size of the gui window in pixels. */
	private var guiH: Int = 166

	protected var title: String = null
	private var background: ResourceLocation = null

	private val textFieldList: util.List[GuiTextField] = new util.ArrayList[GuiTextField]()

	this.setupGui(null, null)
	this.mc = Minecraft.getMinecraft

	def setupGui(title: String, background: ResourceLocation): Unit = {
		this.title = title
		this.background = background

		if (this.title == null)
			this.title = ""

	}

	def setupTextField(textField: GuiTextField, maxStringLength: Int): Unit = {
		textField.setTextColor(-1)
		textField.setDisabledTextColour(-1)
		textField.setEnableBackgroundDrawing(true)
		textField.setMaxStringLength(maxStringLength)
		textField.setText("")
		this.textFieldList.add(textField)
	}

	protected def addButton(button: GuiButton): Unit

	protected def setSize(x: Int, y: Int): Unit = {
		this.guiW = x
		this.guiH = y
		this.getX
		this.getY
	}

	def getWidth: Int = {
		this.guiW
	}

	def getHeight: Int = {
		this.guiH
	}

	def getX: Int = {
		this.guiX = (this.width - this.guiW) / 2
		this.guiX
	}

	def getY: Int = {
		this.guiY = (this.height - this.guiH) / 2
		this.guiY
	}

	def getCenterX: Int = this.getX + (this.getWidth / 2)

	def getCenterY: Int = this.getY + (this.getHeight / 2)

	override def initGui(): Unit = {
		super.initGui()

		this.getX
		this.getY

		Keyboard.enableRepeatEvents(true)

	}

	override def keyTyped(letter: Char, key: Int): Unit = {
		var containsField: Boolean = false
		for (i <- 0 until this.textFieldList.size()) {
			val textField: GuiTextField = this.textFieldList.get(i)
			if (this.canKeyType(textField, letter, key) && textField.textboxKeyTyped(letter, key)) {
				this.sendKeyPacket(textField)
				this.onKeyTyped(textField)
				containsField = true
			}
		}
		if (!containsField) {
			super.keyTyped(letter, key)
		}
	}

	def canKeyType(textField: GuiTextField, letter: Char, key: Int): Boolean = {
		true
	}

	def onKeyTyped(textField: GuiTextField): Unit = {}

	private def sendKeyPacket(textField: GuiTextField): Unit = {
		val packetBuffer = new PacketBuffer(Unpooled.buffer())
		try {
			packetBuffer.writeString(textField.getText)
			this.mc.getNetHandler
					.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", packetBuffer))
		}
		catch {
			case e: Exception =>
				LogManager.getLogger.error("Couldn\'t send text field info", e)
		}
		finally {
			packetBuffer.release()
		}

	}

	override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
		super.mouseClicked(mouseX, mouseY, mouseButton)

		for (i <- 0 until this.textFieldList.size()) {
			val textField: GuiTextField = this.textFieldList.get(i)
			textField.mouseClicked(mouseX, mouseY, mouseButton)
		}
	}

	override def onGuiClosed(): Unit = {
		super.onGuiClosed()
		Keyboard.enableRepeatEvents(false)
	}

	override def doesGuiPauseGame(): Boolean = {
		false
	}

	override def drawScreen(mouseX: Int, mouseY: Int, renderPartialTicks: Float): Unit = {
		this.drawGuiBackgroundLayer(mouseX, mouseY, renderPartialTicks)
		this.drawGuiForegroundLayer(mouseX, mouseY, renderPartialTicks)

		this.guiScreenDrawScreen(mouseX, mouseY, renderPartialTicks)

		this.drawHoverInformation(mouseX, mouseY, renderPartialTicks)

	}

	protected def guiScreenDrawScreen(mouseX: Int, mouseY: Int, renderPartialTicks: Float): Unit = {
		super.drawScreen(mouseX, mouseY, renderPartialTicks)
	}

	private var mouseOffset_full: (Float, Float) = (0, 0)
	private var mouseDrag_startOffset: (Float, Float) = null
	private var mouseDrag_start: (Int, Int) = null

	protected def getMouseDraggedOffset: (Float, Float) = (this.mouseOffset_full._1, this.mouseOffset_full._2)

	protected def drawGuiBackgroundLayer(mouseX: Int, mouseY: Int,
			renderPartialTicks: Float): Unit = {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)

		if (Mouse.isButtonDown(0)) {
			if (this.mouseDrag_start == null) {
				this.mouseDrag_start = (mouseX, mouseY)
				this.mouseDrag_startOffset = this.mouseOffset_full
			} else {
				val offsetX_sinceStart = mouseX - this.mouseDrag_start._1
				val offsetY_sinceStart = mouseY - this.mouseDrag_start._2
				this.mouseOffset_full = (this.mouseDrag_startOffset._1 + offsetX_sinceStart,
						this.mouseDrag_startOffset._2 + offsetY_sinceStart)
			}
		} else if (this.mouseDrag_start != null) {
			this.mouseDrag_start = null
			this.mouseDrag_startOffset = null
		}

		this.drawGuiBackground()

		for (i <- 0 until this.textFieldList.size()) {
			val textField: GuiTextField = this.textFieldList.get(i)
			textField.drawTextBox()
		}

	}

	protected def drawGuiBackground(): Unit = {
		if (this.hasBackground) {
			this.bindBackground()
			this.drawTexturedModalRect(this.getX, this.getY, 0, 0, this.getWidth, this.getHeight)
		}
	}

	protected def bindBackground(): Unit =
		if (this.hasBackground) Rendering.bindResource(this.getBackground)

	protected def hasBackground: Boolean = this.background != null

	protected def getBackground: ResourceLocation = {
		this.background
	}

	protected def drawGuiForegroundLayer(mouseX: Int, mouseY: Int,
			renderPartialTicks: Float): Unit = {
		if (this.title != null) {
			this.drawTitle(
				this.getX + (this.getWidth / 2) - (this.getStringWidth(this.title) / 2),
				this.getY + 5
			)
		}
	}

	protected def drawTitle(x: Int, y: Int): Unit = {
		this.drawString(this.title, x, y)
	}

	protected def drawHoverInformation(mouseX: Int, mouseY: Int,
			renderPartialTicks: Float): Unit = {
		val hoverInfo: util.List[String] = new util.ArrayList[String]()

		this.addInformationOnHover(mouseX, mouseY, renderPartialTicks, hoverInfo)

		if (!hoverInfo.isEmpty) {
			this.renderHoverInformation(mouseX, mouseY, hoverInfo)
		}
	}

	protected def addInformationOnHover(mouseX: Int, mouseY: Int,
			renderPartialTicks: Float, hoverInfo: util.List[String]): Unit = {}

	protected def renderHoverInformation(mouseX: Int, mouseY: Int,
			hoverInfo: util.List[String]): Unit

	def drawString(string: String, x: Int, y: Int): Unit = {
		this.drawString(string, x, y, this.defaultTextColor)
	}

	def drawString(string: String, x: Int, y: Int, color: Int): Unit

	def getStringWidth(string: String): Int

	def isMouseInArea(xywh: (Int, Int, Int, Int), mouseX: Int, mouseY: Int): Boolean =
		this.isMouseInArea(xywh._1, xywh._2, xywh._3, xywh._4, mouseX, mouseY)

	def isMouseInArea(x: Int, y: Int, w: Int, h: Int, mouseX: Int,
			mouseY: Int): Boolean = {
		(x <= mouseX) && (mouseX <= x + w) && (y <= mouseY) && (mouseY <= y + h)
	}

	protected def bindTexture(rl: ResourceLocation): Unit = Rendering.bindResource(rl)

	def drawTexturedModalRect(xy: (Int, Int), wh: (Int, Int), uv: (Int, Int)): Unit =
		this.drawTexturedModalRect(xy._1, xy._2, uv._1, uv._2, wh._1, wh._2)

	def drawModularRect(pos: (Int, Int), uv: (Float, Float), actualSize: (Int, Int),
			renderedSize: (Int, Int), imgSize: (Float, Float)): Unit =
		Rendering.drawTextureWithSizes(pos, uv, actualSize, renderedSize, imgSize)

	protected def drawLine(x1: Int, y1: Int, x2: Int, y2: Int, thickness: Int, color: Int): Unit = {
		GL11.glDisable(3553)

		this.applyColor(color)

		GL11.glEnable(2848)
		GL11.glLineWidth(1.0F + thickness * this.width / 500.0F)

		GL11.glBegin(1)

		GL11.glVertex3f(x1, y1, 0.0F)
		GL11.glVertex3f(x2, y2, 0.0F)

		GL11.glEnd()

		GL11.glEnable(3553)
	}

	protected def applyColor(color: Int): Unit = {
		val a: Float = (color >> 24 & 0xFF) / 255.0F
		val r: Float = (color >> 16 & 0xFF) / 255.0F
		val g: Float = (color >> 8 & 0xFF) / 255.0F
		val b: Float = (color & 0xFF) / 255.0F

		GL11.glColor4f(r, g, b, a)
	}

}
