package temportalist.origin.api.common.utility

import java.util.UUID

import com.mojang.authlib.GameProfile
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.server.MinecraftServer
import net.minecraft.server.management.PlayerProfileCache
import net.minecraft.util.ChatComponentText
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
object Players {

	def getCache: PlayerProfileCache = MinecraftServer.getServer.getPlayerProfileCache

	def getGameProfile(uuid: UUID): GameProfile = this.getCache.getProfileByUUID(uuid)

	def getUserName(id: String): String = this.getUserName(UUID.fromString(id))

	def getUserName(id: UUID): String = {
		this.getGameProfile(id) match {
			case profile: GameProfile => profile.getName
			case _ => null
		}
	}

	def isOnline(uuid: UUID): Boolean = this.getPlayerOnline(uuid) != null

	def getPlayerOnline(username: String): EntityPlayer = {
		this.getCache.getGameProfileForUsername(username) match {
			case profile: GameProfile => this.getPlayerOnline(profile.getId)
			case _ => null
		}
	}

	def getPlayerOnline(uuid: UUID): EntityPlayer = {
		MinecraftServer.getServer.getConfigurationManager.getPlayerByUUID(uuid)
	}

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
