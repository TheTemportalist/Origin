package temportalist.origin.api.common.item

import java.util

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{ActionResult, EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.api.common.IModDetails

/**
  * A base class for Item registration
  * @param mod The mod details from which this item was created
  * @param name The name for this item. If null, [[Class#getClass#getSimpleName]] will be used.
  *
  * Created by TheTemportalist on 4/10/2016.
  * @author TheTemportalist
  */
class ItemBase(
		mod: IModDetails,
		var name: String = null,
		private val itemMetaRange: Range = Range.apply(0, 1)
) extends Item() {

	if (this.name == null) this.name = this.getClass.getSimpleName
	this.setRegistryName(this.mod.getModId, this.name)
	this.setUnlocalizedName(this.mod.getModId + ":" + this.name)
	GameRegistry.register(this)

	final def getItemMetaRange: Range = this.itemMetaRange

	// ~~~~~~~~~~ Helpers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	  * Returns an PASS action
	  *
	  * @param itemStack The [[ItemStack]]
	  * @return An [[ActionResult]] with [[EnumActionResult.PASS]] and the itemstack
	  */
	final def getActionPASS(itemStack: ItemStack): ActionResult[ItemStack] =
		new ActionResult[ItemStack](EnumActionResult.PASS, itemStack)

	/**
	  * Returns an SUCCESS action
	  *
	  * @param itemStack The [[ItemStack]]
	  * @return An [[ActionResult]] with [[EnumActionResult.SUCCESS]] and the itemstack
	  */
	final def getActionSUCCESS(itemStack: ItemStack): ActionResult[ItemStack] =
		new ActionResult[ItemStack](EnumActionResult.SUCCESS, itemStack)

	/**
	  * Returns an FAIL action
	  *
	  * @param itemStack The [[ItemStack]]
	  * @return An [[ActionResult]] with [[EnumActionResult.FAIL]] and the itemstack
	  */
	final def getActionFAIL(itemStack: ItemStack): ActionResult[ItemStack] =
		new ActionResult[ItemStack](EnumActionResult.FAIL, itemStack)

	// ~~~~~~~~~~ Documentation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	  * Provides interactions when items are clicked
	  *
	  * @param itemStackIn The [[ItemStack]] the playerIn is using
	  * @param worldIn The [[World]] the world the playerIn in
	  * @param playerIn the [[EntityPlayer]]
	  * @param hand The [[EnumHand]] the item is in
	  * @return An [[ActionResult]]. Recommended to use [[getActionPASS]], [[getActionSUCCESS]] or [[getActionFAIL]]
	  */
	override def onItemRightClick(itemStackIn: ItemStack, worldIn: World, playerIn: EntityPlayer,
			hand: EnumHand): ActionResult[ItemStack] = getActionPASS(itemStackIn)

	/**
	  * Provides iterations when items are used on a block
	  *
	  * @param stack The [[ItemStack]] the playerIn is using
	  * @param playerIn the [[EntityPlayer]]
	  * @param worldIn The [[World]] the world the playerIn in
	  * @param pos The [[BlockPos]] of the block that was clicked
	  * @param hand The [[EnumHand]] the item is in
	  * @param facing The [[EnumFacing]] of the block which was clicked (the side clicked on)
	  * @param hitX A [[Float]] percentage of the side of the block on the X-Axis from block origin
	  * @param hitY A [[Float]] percentage of the side of the block on the Y-Axis from block origin
	  * @param hitZ A [[Float]] percentage of the side of the block on the Z-Axis from block origin
	  * @return An [[EnumActionResult]]
	  */
	override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos,
			hand: EnumHand, facing: EnumFacing,
			hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = EnumActionResult.PASS

	/**
	  * Add information to the item's tooltip
	  *
	  * @param itemStack The [[ItemStack]]
	  * @param playerIn The [[EntityPlayer]]
	  * @param tooltip The tooltip list
	  * @param advanced true if [[net.minecraft.client.settings.GameSettings.advancedItemTooltips]] (registry name, integer id, durability)
	  */
	@SideOnly(Side.CLIENT)
	override def addInformation(itemStack: ItemStack, playerIn: EntityPlayer,
			tooltip: util.List[String], advanced: Boolean): Unit = {}

}
