package temportalist.origin.screwdriver.common.behaviors

import java.util

import com.temportalist.origin.api.common.lib.NameParser
import com.temportalist.origin.api.common.resource.EnumResource
import com.temportalist.origin.screwdriver.api.{Behavior, BehaviorType}
import com.temportalist.origin.screwdriver.common.behaviors.immersiveengineering.BehaviorIEHammer
import com.temportalist.origin.screwdriver.common.{AddonScrewdriver, CompatibleAPI}
import cpw.mods.fml.common.eventhandler.Event.Result
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.event.entity.player.PlayerInteractEvent

/**
 * Created by TheTemportalist on 12/20/2015.
 */
object BehaviorWrench extends Behavior("Wrench") {

	override def isValidStackForSimulation(stack: ItemStack): Boolean = {
		val item = stack.getItem
		if (CompatibleAPI.IE.isModLoaded) {
			var mod_id = NameParser.getName(stack)
			mod_id = mod_id.substring(0, mod_id.indexOf(':'))
			if (mod_id == CompatibleAPI.IE.getModid)
				return BehaviorIEHammer.isValidStackForSimulation(stack)
		}
		if (CompatibleAPI.AE.isAPILoaded) {
			if (item.isInstanceOf[appeng.api.implementations.items.IAEWrench]) return true
		}
		if (CompatibleAPI.BUILDCRAFT.isAPILoaded) {
			if (item.isInstanceOf[buildcraft.api.tools.IToolWrench]) return true
		}
		if (CompatibleAPI.COFH.isAPILoaded) {
			if (item.isInstanceOf[cofh.api.item.IToolHammer]) return true
		}
		if (CompatibleAPI.ENDERIO.isAPILoaded) {
			if (item.isInstanceOf[crazypants.enderio.api.tool.ITool]) return true
		}
		if (CompatibleAPI.MFR.isModLoaded) {
			if (item.isInstanceOf[powercrystals.minefactoryreloaded.api.IMFRHammer]) return true
		}
		false
	}

	override def postInit(): Unit = {
		super.postInit()
		AddonScrewdriver.loadResource("wrench",
			(EnumResource.TEXTURE_ITEM, "moduleIcons/wrench.png"))
	}

	def getBehaviorType: BehaviorType = BehaviorType.ACTIVE

	@SideOnly(Side.CLIENT)
	override protected def getTexture: ResourceLocation = AddonScrewdriver.getResource("wrench")

	override def onItemUseFirst(container: ItemStack, source: ItemStack, player: EntityPlayer,
			world: World, x: Int, y: Int, z: Int, side: Int,
			hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		{
			val event = new PlayerInteractEvent(
				player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, x, y, z, side, world)
			if (MinecraftForge.EVENT_BUS.post(event) ||
					event.getResult == Result.DENY ||
					event.useBlock == Result.DENY ||
					event.useItem == Result.DENY) return false
		}
		val block = world.getBlock(x, y, z)
		var retVar: Boolean = false
		if (block != null) {
			if (player.isSneaking) {
				if (CompatibleAPI.COFH.isAPILoaded &&
						block.isInstanceOf[cofh.api.block.IDismantleable]) {
					val dismantleable = block.asInstanceOf[cofh.api.block.IDismantleable]
					if (dismantleable.canDismantle(player, world, x, y, z) && !world.isRemote)
						dismantleable.dismantleBlock(player, world, x, y, z, false)
					retVar = true
				}
				else if (CompatibleAPI.IE.isModLoaded)
					retVar = BehaviorIEHammer.dismantle(player, world, x, y, z)
			}
			else {
				if (block == Blocks.chest &&
						block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
					// This works around a forge bug where you can rotate double chests to invalid directions
					val te = world.getTileEntity(x, y, z).asInstanceOf[TileEntityChest]
					if (te.adjacentChestXNeg != null || te.adjacentChestXPos != null ||
							te.adjacentChestZNeg != null || te.adjacentChestZPos != null) {
						// Render master is always the chest to the negative direction
						val masterChest =
							if (te.adjacentChestXNeg == null && te.adjacentChestZNeg == null) te
							else if (te.adjacentChestXNeg == null) te.adjacentChestZNeg
							else te.adjacentChestXNeg
						if (masterChest != te) {
							val meta = world.getBlockMetadata(
								masterChest.xCoord, masterChest.yCoord, masterChest.zCoord)
							world.setBlockMetadataWithNotify(
								masterChest.xCoord, masterChest.yCoord, masterChest.zCoord,
								meta ^ 1, 3)
						} else {
							// If this is the master chest, we can just rotate twice
							block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))
						}
					}
					retVar = true
				}
			}
		}
		if (retVar) player.swingItem()
		retVar && !world.isRemote
	}

	override def getBehaviorToolClasses(container: ItemStack, source: ItemStack,
			toolClasses: util.Set[String]): Unit = {
		super.getBehaviorToolClasses(container, source, toolClasses)
		toolClasses.add("IE_HAMMER")
	}

}
