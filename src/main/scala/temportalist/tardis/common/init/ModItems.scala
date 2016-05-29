package temportalist.tardis.common.init

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.EnumFacing.Axis
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import temportalist.origin.api.common.item.ItemBase
import temportalist.origin.api.common.lib.Vect
import temportalist.origin.foundation.common.registers.ItemRegister
import temportalist.tardis.common.entity.EntityTardis

/**
  *
  * Created by TheTemportalist on 5/27/2016.
  *
  * @author TheTemportalist
  */
object ModItems extends ItemRegister {

	var placer: Item = null

	override def register(): Unit = {

		this.placer = new ItemBase(this.getMod, name = "tardis") {

			override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World,
					pos: BlockPos, hand: EnumHand, facing: EnumFacing,
					hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {

				if (!worldIn.isRemote) {
					val entity = new EntityTardis(worldIn)
					val tardisPos = new Vect(pos) + Vect.CENTER.suppressAxisGet(Axis.Y) + facing
					entity.setPosition(tardisPos.x, tardisPos.y, tardisPos.z)
					worldIn.spawnEntityInWorld(entity)
				}

				EnumActionResult.SUCCESS
			}

		}
		this.placer.setCreativeTab(CreativeTabs.TRANSPORTATION)

	}

}
