package temportalist.origin.api.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraftforge.fml.relauncher.{SideOnly, Side}

/**
 *
 *
 * @author  TheTemportalist  5/8/15
 */
@SideOnly(Side.CLIENT)
trait IButton extends GuiButton {

	def isHoveringOver(mouse: (Int, Int)): Boolean =
		mouse._1 >= this.xPosition && mouse._1 <= this.xPosition + this.width &&
				mouse._2 >= this.yPosition && mouse._2 <= this.yPosition + this.height

	protected def position: (Int, Int) = (this.xPosition, this.yPosition)

	protected def size: (Int, Int) = (this.width, this.height)

}
