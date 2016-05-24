package temportalist.origin.foundation.common.tile

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.relauncher.Side
import temportalist.origin.foundation.common.network.PacketTileCallback

/**
 *
 *
 * @author TheTemportalist
 */
trait IPacketCallback extends TileEntity {

	def packetCallback(packet: PacketTileCallback, side: Side): Unit

}
