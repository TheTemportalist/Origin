package temportalist.origin.api.common.utility

import java.util.UUID

import com.mojang.authlib.GameProfile
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.server.MinecraftServer
import net.minecraft.server.management.PlayerProfileCache
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
@Deprecated
object Players {

	def getServer: MinecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance

	def getCache: PlayerProfileCache = this.getServer.getPlayerProfileCache

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

	/**
	  * Use [[net.minecraft.server.management.PlayerList]]
	  * @param uuid
	  * @return
	  */
	@Deprecated
	def getPlayerOnline(uuid: UUID): EntityPlayer = {
		this.getServer.getPlayerList.getPlayerByUUID(uuid)
	}

	def message(player: EntityPlayer, message: String): Unit = {
		player.addChatComponentMessage(new TextComponentString(message))
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

	private def getReach_server(mp: EntityPlayerMP): Double = mp.interactionManager.getBlockReachDistance

}
