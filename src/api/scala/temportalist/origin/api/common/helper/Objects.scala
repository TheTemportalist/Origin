package temportalist.origin.api.common.helper

import net.minecraft.block.Block
import net.minecraft.item.Item

/**
  * Provides simple methods for boolean returns
  *
  * Created by TheTemportalist on 4/9/2016.
  * @author TheTemportalist
  */
object Objects {

	def isBlock(item: Item): Boolean = Block.getBlockFromItem(item) != null

}
