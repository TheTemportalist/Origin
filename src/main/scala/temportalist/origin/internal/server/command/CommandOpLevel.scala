package temportalist.origin.internal.server.command

import com.mojang.authlib.GameProfile
import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.server.MinecraftServer
import net.minecraft.server.management.UserListOpsEntry
import net.minecraft.util.ChatComponentText
import net.minecraftforge.fml.common.ObfuscationReflectionHelper

/**
  * Created by TheTemportalist on 1/17/2016.
  */
object CommandOpLevel extends CommandBase with ICommand {

	override def getCommandName: String = "oplevel"

	override def getUsage: String = "commands.oplevel.usage"

	override def processCommand(sender: ICommandSender, args: Array[String]): Unit = {
		// /oplevel set <player> <level>
		// /oplevel get <player>
		if (args.length < 2) return
		args(0) match {
			case "set" =>
				val profile = this.getPlayerProfile(sender, args(1), checkSender = false)
				if (profile != null) this.setOpLevel(profile, args(2).toInt)
			case "get" =>
				val profile = this.getPlayerProfile(sender, args(1), checkSender = false)
				if (profile != null) {
					val entry = MinecraftServer.getServer.getConfigurationManager.getOppedPlayers.getEntry(profile)
					val level = if (entry == null) 0 else entry.getPermissionLevel
					sender.addChatMessage(new ChatComponentText("Player " + profile.getName +
							" has a an op level of " + level))
				}
			case _ =>
		}
	}

	def setOpLevel(profile: GameProfile, level: Int): Unit = {
		val config = MinecraftServer.getServer.getConfigurationManager
		val ops = config.getOppedPlayers
		ops.getEntry(profile) match {
			case entry: UserListOpsEntry =>
				ObfuscationReflectionHelper.setPrivateValue(
					classOf[UserListOpsEntry], entry, level, 0)
			case _ =>
				ops.addEntry(new UserListOpsEntry(profile, level, ops.func_183026_b(profile)))
		}
	}

	override def getRequiredPermissionLevel: Int = 3

}
