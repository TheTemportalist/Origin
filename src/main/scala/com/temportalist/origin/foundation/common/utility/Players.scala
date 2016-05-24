package com.temportalist.origin.foundation.common.utility

import java.util
import java.util.UUID

import com.temportalist.origin.api.common.utility.{Scala, Teleport}
import com.temportalist.origin.internal.common.Origin
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.PlayerEvent
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChatComponentText

/**
 *
 *
 * @author TheTemportalist
 */
object Players {

	final val onlinePlayers: util.List[UUID] = new util.ArrayList[UUID]
	// todo save to disk
	final val idToUsername: util.HashMap[UUID, String] = new util.HashMap[UUID, String]
	final val usernameToId: util.HashMap[String, UUID] = new util.HashMap[String, UUID]

	def getUserName(id: String): String = this.getUserName(UUID.fromString(id))

	def getUserName(id: UUID): String = {
		this.idToUsername.get(id)
	}

	def isOnline(uuid: UUID): Boolean = this.onlinePlayers.contains(uuid)

	def getPlayer(username: String): EntityPlayer = this.getPlayerOnline(this.usernameToId.get(username))

	def getPlayerOnline(uuid: UUID): EntityPlayer = {
		if (uuid == null) Origin.log("null uuid")
		Scala.foreach(MinecraftServer.getServer.getConfigurationManager.playerEntityList
				.asInstanceOf[util.List[EntityPlayer]], (index: Int, player: EntityPlayer) => {
			if (player.getGameProfile.getId == uuid) return player
		})
		null
	}

	@SubscribeEvent
	def login(event: PlayerEvent.PlayerLoggedInEvent) {
		val id: UUID = event.player.getGameProfile.getId
		this.idToUsername.put(id, event.player.getCommandSenderName)
		this.usernameToId.put(event.player.getCommandSenderName, id)
		this.onlinePlayers.add(id)
	}

	@SubscribeEvent
	def logout(event: PlayerEvent.PlayerLoggedOutEvent) {
		this.onlinePlayers.remove(event.player.getGameProfile.getId)
	}

	/*
	def getPlayer(senderNameORuuid: AnyRef): EntityPlayerMP = {
		val players: java.util.List[_] =
			MinecraftServer.getServer.getConfigurationManager.playerEntityList
		for (i <- 0 until players.size()) {
			players.get(i) match {
				case player: EntityPlayerMP =>
					if (player.getCommandSenderName.equals(senderNameORuuid) ||
							player.getUniqueID.equals(senderNameORuuid)) {
						return player
					}
				case _ =>
			}
		}
		null
	}
	*/

	def forceMoveInDirection(player: EntityPlayerMP, distance: Double,
			considerObstacles: Boolean): Unit = {
		val rotH: Double = Math.toRadians(player.rotationYaw)
		val rotV: Double = Math.toRadians(player.rotationPitch)
		val dx: Double = -Math.sin(rotH) * distance
		val dy: Double = Math.sin(rotV) * distance
		val dz: Double = Math.cos(rotH) * distance

		if (considerObstacles) {
			// todo how to determine dy correctly?
			player.moveEntity(dx, 0, dz)
		}
		else {
			Teleport.toPoint(player, player.posX + dx, player.posY + dy, player.posZ + dz)
		}
	}

	def message(player: EntityPlayer, message: String): Unit = {
		player.addChatComponentMessage(new ChatComponentText(message))
	}

	def getReachDistance(player: EntityPlayer): Double = {
		if (player.getEntityWorld.isRemote)
			this.getReach_client
		else player match {
			case mp: EntityPlayerMP => this.getReach_server(mp)
			case _ => 5.0D
		}
	}

	@SideOnly(Side.CLIENT)
	private def getReach_client: Double = Minecraft.getMinecraft.playerController.getBlockReachDistance

	private def getReach_server(mp: EntityPlayerMP): Double = mp.theItemInWorldManager.getBlockReachDistance

}
