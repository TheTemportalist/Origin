package temportalist.origin.api.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.origin.api.common.IModDetails

/**
  * A basic wrapping class for blocks. Registers Blocks and ItemBlocks for you.
  *
  * @param mod The mod this block belongs to
  * @param name The name. Will use [[Class#getClass#getSimpleName]] if null
  * @param material The material, [[net.minecraft.block.material.Material.GROUND]] by default
  * @param mapColor The map color, [[Material.GROUND#getMaterialMapColor]] by default
  * @param mHasItemBlock Whether this block should have an item block, true by default
  *
  * Created by TheTemportalist on 4/9/2016.
  * @author TheTemportalist
  */
class BlockBase(
		mod: IModDetails,
		var name: String = null,
		material: Material = Material.GROUND, mapColor: MapColor = null,
		private var mHasItemBlock: Boolean = true, private var itemMetaRange: Range = Range.apply(0, 1)
) extends Block(
	material,
	if (mapColor == null) material.getMaterialMapColor else mapColor
) {

	if (this.name == null) this.name = this.getClass.getSimpleName
	this.setRegistryName(this.mod.getModId, this.name)
	this.setUnlocalizedName(this.mod.getModId + ":" + this.name)
	GameRegistry.register(this)

	private val itemBlock: ItemBlock = if (this.hasItemBlock) this.createItemBlock() else null
	if (this.hasItemBlock) GameRegistry.register(this.itemBlock.setRegistryName(this.getRegistryName))

	/**
	  * Create the ItemBlock instance. Called on initialization ONLY if [[mHasItemBlock]] is true.
	  * @return the ItemBlock instance, a generic ItemBlock by default
	  */
	def createItemBlock(): ItemBlock = new ItemBlock(this)

	final def hasItemBlock: Boolean = this.mHasItemBlock

	final def getItemBlock: ItemBlock = this.itemBlock

	final def getItemMetaRange: Range = this.itemMetaRange

}
