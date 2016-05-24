package com.temportalist.origin.screwdriver.client

import java.util

import com.temportalist.origin.api.client.gui.GuiContainerBase
import com.temportalist.origin.internal.common.Origin
import com.temportalist.origin.screwdriver.api.BehaviorType
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import com.temportalist.origin.screwdriver.common.behaviors.BehaviorSettings
import com.temportalist.origin.screwdriver.common.container.ContainerBehaviors
import com.temportalist.origin.screwdriver.common.network.PacketUpdateItem
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.FontRenderer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumChatFormatting

import scala.collection.mutable

/**
 * Created by TheTemportalist on 12/21/2015.
 */
@SideOnly(Side.CLIENT)
class GuiBehaviors(p: EntityPlayer)
		extends GuiContainerBase(new ContainerBehaviors(p)) {

	val stack = p.getCurrentEquippedItem
	this.setSize(256, 162)
	this.setupGui("", AddonScrewdriver.getResource("gui_behaviors"))

	// ~~~~~~~~~~~ Modify Tooltips For Items ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def renderToolTip(stack: ItemStack, x: Int, y: Int): Unit = {
		val list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips)
		val list2 = new util.ArrayList[String]()
		for (i <- 0 until list.size()) {
			if (i == 0) list2.add(stack.getRarity.rarityColor + list.get(i).asInstanceOf[String])
			else list2.add(EnumChatFormatting.GRAY + list.get(i).asInstanceOf[String])
		}
		this.modifyToolTip(stack, list2)
		val font: FontRenderer = stack.getItem.getFontRenderer(stack)
		drawHoveringText(list2, x, y, if (font == null) fontRendererObj else font)
	}

	def modifyToolTip(stack: ItemStack, tooltip: util.List[String]): Unit = {
		tooltip.add(EnumChatFormatting.DARK_PURPLE + "Provides:")
		AddonScrewdriver.getBehaviorIDsProvided(stack).foreach(globalID => {
			tooltip.add(EnumChatFormatting.LIGHT_PURPLE +
					AddonScrewdriver.getBehaviorNameByGlobalID(globalID))
		})
	}

	// ~~~~~~~~~~~ Drawing Behaviors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// hotBar: (x, y, w, h) -> (hotBar index, globalID)
	private val hotBarMap = mutable.Map[(Int, Int, Int, Int), (Int, Int)]()
	// active, toggle, passive: (x, y, w, h) -> (type id, globalID)
	private val behaviorMap = mutable.Map[(Int, Int, Int, Int), (Int, Int)]()
	private var behaviorDragged_ID = -1

	override protected def drawGuiBackgroundLayer(mouseX: Int, mouseY: Int,
			renderPartialTicks: Float): Unit = {
		super.drawGuiBackgroundLayer(mouseX, mouseY, renderPartialTicks)

		val guiStart = (this.getX(), this.getY())
		val hotBar = (guiStart._1 + 79, guiStart._2 + 5)

		// do an isBackground check to prevent over use of texture binding
		this.drawSlots(guiStart._1 + 7, guiStart._2 + 4)
		this.drawHotBar(hotBar._1, hotBar._2, isBackground = true)
		this.drawBehaviorAreas(guiStart, isBackground = true)
		this.drawHotBar(hotBar._1, hotBar._2, isBackground = false)
		this.drawBehaviorAreas(guiStart, isBackground = false)

		if (this.behaviorDragged_ID > 0) {
			AddonScrewdriver.getBehaviorByGlobalID(this.behaviorDragged_ID).draw(this.mc,
				mouseX, mouseY, this.zLevel, 16, 16, false)
		}

	}

	private def drawSlots(xGlobal: Int, yGlobal: Int): Unit = {
		for (i <- 0 until AddonScrewdriver.NBTBehaviorHelper.getInventorySize(this.stack)) {
			val x = xGlobal + 18 * (i % 3)
			val y = yGlobal + 18 * (i / 3)
			this.drawTexturedModalRect((x, y), (18, 18), (7, 82))
		}
	}

	private def drawBehaviorFrame(xGlobal: Int, yGlobal: Int, xUnits: Int, yUnits: Int): Unit = {
		this.drawTexturedModalRect(
			(xGlobal + 18 * xUnits, yGlobal + 18 * yUnits), (18, 18), (0, 177))
	}

	private def drawHotBar(xGlobal: Int, yGlobal: Int, isBackground: Boolean): Unit = {
		// checking isBackground for hotBarIDs prevents loading for the background layer,
		// while also not processing it on each iteration
		val hotBarIDs =
			if (isBackground) null
			else AddonScrewdriver.NBTBehaviorHelper.getHotBarGlobalIDs(this.stack)
		val iconSize = 16
		val iconInc = 2
		val frameSize = iconSize + iconInc
		for (i <- 0 until AddonScrewdriver.NBTBehaviorHelper.getHotBarSize(this.stack)) {
			if (isBackground) {
				if (i > 0) this.drawBehaviorFrame(xGlobal, yGlobal, i - 1, 0)
			}
			else {
				// check to make sure the rendering hotbar index is in the array of IDs
				// and that it is not the BehaviorSettings
				val shiftedIndex = i + 1
				if (i < hotBarIDs.length) {
					val x = xGlobal + frameSize * i + 1
					val y = yGlobal + 1
					if (shiftedIndex < hotBarIDs.length) {
						this.hotBarMap((x, y, iconSize, iconSize)) = (i, hotBarIDs(shiftedIndex))
						if (hotBarIDs(shiftedIndex) >= 0 &&
								hotBarIDs(shiftedIndex) !=
										AddonScrewdriver.BEHAVIOR_SETTINGS_GLOBAL_ID)
							AddonScrewdriver.getBehaviorByGlobalID(
								hotBarIDs(shiftedIndex)).draw(this.mc,
								x, y, this.zLevel, iconSize, iconSize, false)
					}
				}
			}
		}
	}

	def drawBehaviorAreas(globalStart: (Int, Int), isBackground: Boolean): Unit = {
		val iconSize = 16
		val iconInc = 2
		BehaviorType.values().foreach(behaviorType => {
			val behaviors =
				if (isBackground) null
				else AddonScrewdriver.NBTBehaviorHelper.getObtainedBehaviors(stack, behaviorType)
			val areaStartX = behaviorType.getGuiStartX
			val areaStartY = behaviorType.getGuiStartY
			val rowSize = behaviorType.getGuiRowSize
			for (i <- 0 until
					AddonScrewdriver.NBTBehaviorHelper.getBehaviorSize(this.stack, behaviorType)) {
				if (isBackground) {
					this.drawBehaviorFrame(
						globalStart._1 + areaStartX,
						globalStart._2 + areaStartY,
						i % rowSize, i / rowSize
					)
				}
				else {
					if (i < behaviors.length) {
						val index = if (behaviorType == BehaviorType.ACTIVE) i - 1 else i
						if (behaviors(i) >= 0 &&
								behaviors(i) != AddonScrewdriver.BEHAVIOR_SETTINGS_GLOBAL_ID) {
							val x = globalStart._1 + areaStartX +
									(index % rowSize) * (iconSize + iconInc) + 1
							val y = globalStart._2 + areaStartY +
									(index / rowSize) * (iconSize + iconInc) + 1
							AddonScrewdriver.getBehaviorByGlobalID(behaviors(i)).draw(this.mc,
								x, y, this.zLevel, iconSize, iconSize, false)
							this.behaviorMap((x, y, iconSize, iconSize)) =
									(behaviorType.getID, behaviors(i))
						}
					}
				}
			}
		})
	}

	// ~~~~~~~~~~~ Behaviors Hover Info ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override protected def addInformationOnHover(mouseX: Int, mouseY: Int,
			renderPartialTicks: Float, hoverInfo: util.List[String]): Unit = {
		val globalID = this.getBehaviorIDAtMouse(mouseX, mouseY)._2._2
		if (globalID >= 0)
			AddonScrewdriver.getBehaviorByGlobalID(globalID).addInformationOnHover(hoverInfo)
	}

	// ~~~~~~~~~~~ Dragging Behaviors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
		super.mouseClicked(mouseX, mouseY, mouseButton)
		val behaviorDetails = this.getBehaviorIDAtMouse(mouseX, mouseY)
		val globalID = behaviorDetails._2._2
		if (!behaviorDetails._1) {
			if (mouseButton > 0)
				this.putBehaviorIDInHotBar(behaviorDetails._2._1 + 1, -1)
		}
		else if (globalID >= 0) {
			val typeID = behaviorDetails._2._1
			if (typeID == BehaviorType.TOGGLE.getID)
				this.toggleBehavior(globalID)
			else if (typeID == BehaviorType.ACTIVE.getID && globalID > 0)
				this.behaviorDragged_ID = globalID
		}
	}

	override def mouseMovedOrUp(mouseX: Int, mouseY: Int, which: Int): Unit = {
		super.mouseMovedOrUp(mouseX, mouseY, which)
		// which: -1 = move, 0 or 1 = mouse up
		if (which > -1) {
			val behaviorDetails = this.getBehaviorIDAtMouse(mouseX, mouseY)
			if (!behaviorDetails._1 && behaviorDetails._2._1 >= 0)
				this.putBehaviorIDInHotBar(behaviorDetails._2._1 + 1, this.behaviorDragged_ID)
			this.behaviorDragged_ID = -1
		}
	}

	// ~~~~~~~~~~~ Behavior Utilities ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 *
	 * @return isBehaviorArea, Value1(hotBar index, or behaviorID), Value2(globalID)
	 */
	def getBehaviorIDAtMouse(mouseX: Int, mouseY: Int): (Boolean, (Int, Int)) = {
		// key: x, y, w, h
		// value: unknown, globalID
		this.hotBarMap.foreach(keyValue =>
			if (this.isMouseInArea(keyValue._1, mouseX, mouseY)) return (false, keyValue._2))
		this.behaviorMap.foreach(keyValue =>
			if (this.isMouseInArea(keyValue._1, mouseX, mouseY)) return (true, keyValue._2))
		(false, (-1, -1))
	}

	def toggleBehavior(globalID: Int): Unit = {
		AddonScrewdriver.NBTBehaviorHelper.toggleBehavior(this.stack,
			AddonScrewdriver.getBehaviorByGlobalID(globalID))
		this.updateStack()
	}

	def putBehaviorIDInHotBar(hotBarIndex: Int, globalID: Int): Unit = {
		AddonScrewdriver.NBTBehaviorHelper.putInHotBar(this.stack, hotBarIndex, globalID)
		this.updateStack()
	}

	def updateStack(): Unit = {
		PacketUpdateItem.setStackAtCurrent(this.mc.thePlayer, this.stack)
		new PacketUpdateItem(this.stack).sendToServer(AddonScrewdriver)
	}

}
