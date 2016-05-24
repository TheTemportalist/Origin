package com.temportalist.origin.foundation.common.tile

import com.temportalist.origin.foundation.common.network.PacketTileCallback
import cpw.mods.fml.relauncher.Side
import net.minecraft.tileentity.TileEntity

/**
 *
 *
 * @author TheTemportalist
 */
trait IPacketCallback extends TileEntity {

	def packetCallback(packet: PacketTileCallback, side: Side): Unit

}
