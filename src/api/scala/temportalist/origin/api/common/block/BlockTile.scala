package temportalist.origin.api.common.block

import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import temportalist.origin.api.common.IModDetails

/**
  * A basic wrapping class for [[net.minecraft.tileentity.TileEntity]] blocks. Registers Blocks and ItemBlocks for you.
  *
  * @param mod The mod this block belongs to
  * @param classTile A class extending [[net.minecraft.tileentity.TileEntity]]
  * @param name The name. Will use [[Class#getClass#getSimpleName]] if null
  * @param material The material, [[Material.GROUND]] by default
  * @param mapColor The map color, [[Material.GROUND.getMaterialMapColor]] by default
  * @param hasItemBlock Whether this block should have an item block, true by default
  *
  * Created by TheTemportalist on 4/9/2016.
  * @author TheTemportalist
  */
class BlockTile(
		mod: IModDetails, classTile: Class[_ <: TileEntity],
		name: String = null,
		material: Material = Material.GROUND, mapColor: MapColor = null,
		hasItemBlock: Boolean = true, itemMetaRange: Range = Range.apply(0, 0)
) extends BlockBase(mod,
	name = name,
	material = material, mapColor = mapColor,
	mHasItemBlock = hasItemBlock, itemMetaRange = itemMetaRange
) {

	this.isBlockContainer = true

	override def hasTileEntity(state: IBlockState): Boolean = true

	override def createTileEntity(world: World, state: IBlockState): TileEntity = {
		try
			return this.classTile.newInstance()
		catch {
			case e: InstantiationException => e.printStackTrace()
			case e: IllegalAccessException => e.printStackTrace()
		}
		null
	}

}
