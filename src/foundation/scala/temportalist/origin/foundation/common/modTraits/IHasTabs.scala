package temportalist.origin.foundation.common.modTraits

import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item

import scala.collection.mutable

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
trait IHasTabs {

	private val tabContents = mutable.Map[String, mutable.ListBuffer[AnyRef]]()

	final def addTab(tab: String): Unit = this.tabContents(tab) = mutable.ListBuffer[AnyRef]()

	final def addTabItems(tab: String, elements: Item*): Unit = this.tabContents(tab) ++= elements

	final def addTabBlocks(tab: String, elements: Block*): Unit = this.tabContents(tab) ++= elements

	def getTabIconItemForKey(label: String): Item

	final def initTabs(): Unit = {
		this.tabContents.foreach(tabKeyAndContents => {
			val tab = new CreativeTabs(tabKeyAndContents._1) {
				override def getTabIconItem: Item = getTabIconItemForKey(tabKeyAndContents._1)
			}

			tabKeyAndContents._2.foreach {
				case item: Item => item.setCreativeTab(tab)
				case block: Block => block.setCreativeTab(tab)
			}
		})
	}

}
