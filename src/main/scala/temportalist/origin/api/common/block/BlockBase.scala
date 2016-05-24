package temportalist.origin.api.common.block

import java.util

import net.minecraft.block.Block
import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.origin.api.common.lib.V3O
import temportalist.origin.api.common.rendering.IRenderingObject
import temportalist.origin.api.common.utility.Stacks
import temportalist.origin.foundation.common.register.BlockRegister
import temportalist.origin.internal.common.Origin

/**
  * Created by TheTemportalist on 1/3/2016.
  */
class BlockBase(reg: BlockRegister,
		mat: Material = Material.ground, color: MapColor = null,
		itemBlock: Class[_ <: ItemBlock] = null) extends Block(mat,
		if (color == null) mat.getMaterialMapColor else color) with IRenderingObject {

	/**
	  * Mod ID of the mod this block belongs to
	  */
	private val modID: String = reg.getMod.getModID
	this.setNameAndRegister(this.getClass.getSimpleName, itemBlock)
	reg.addBlock(this)

	// ~~~~~~~~~~~ Getter/Setter functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final def addToOriginTab(): BlockBase = {
		Origin.addBlockToTab(this)
		this
	}

	/**
	  * Local function used to register the block with the game
	  */
	private def setNameAndRegister(name: String, itemBlock: Class[_ <: ItemBlock]): Unit = {
		this.setUnlocalizedName(name)
		if (itemBlock != null) GameRegistry.registerBlock(this, itemBlock, name)
		else GameRegistry.registerBlock(this, name)
	}

	def getModID: String = this.modID

	// ~~~~~~~~~~~ Name Handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getCompoundName: String = this.getModID + ":" + BlockBase.getName(this)

	override def getUnlocalizedName: String = BlockBase.getUnlocalizedName(this)

	// ~~~~~~~~~~~ Additional rendering functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def hasCustomItemModel: Boolean = false

	def usesOBJ: Boolean = false

	// ~~~~~~~~~~~~~~~ Start supered wrappers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState,
			playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float,
			hitZ: Float): Boolean = {
		super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ)
	}

	override def removedByPlayer(world: World, pos: BlockPos, player: EntityPlayer,
			willHarvest: Boolean): Boolean = {
		if (!player.capabilities.isCreativeMode)
			Stacks.spawnDrops(world, new V3O(pos),
				this.getDrops_Pre(world, pos, world.getBlockState(pos), world.getTileEntity(pos))
			)
		super.removedByPlayer(world, pos, player, willHarvest)
	}

	def getDrops_Pre(world: World, pos: BlockPos, state: IBlockState,
			tile: TileEntity): util.List[ItemStack] = {
		super.getDrops(world, pos, state, 0)
	}

	/* Runs on POST block destruction */
	override def getDrops(world: IBlockAccess, pos: BlockPos, state: IBlockState,
			fortune: Int): util.List[ItemStack] = new util.ArrayList[ItemStack]()

}
object BlockBase {

	def getUnlocalizedName(block: Block): String = {
		"tile." + (block match {
			case base: BlockBase => base.getCompoundName
			case _ => this.getName(block)
		})
	}

	def getName(block: Block): String = {
		block match {
			case base: BlockBase => base.getClass.getSimpleName
			case _ => block.getUnlocalizedName
		}
	}

	def getLocalizedName(block: Block): String = {
		I18n.format(this.getUnlocalizedName(block) + ".name").trim
	}

}
