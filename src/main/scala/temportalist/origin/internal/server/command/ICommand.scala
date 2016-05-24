package temportalist.origin.internal.server.command

import java.util.UUID

import com.mojang.authlib.GameProfile
import net.minecraft.command.{CommandBase, ICommandSender, WrongUsageException}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChatComponentText
import temportalist.origin.api.common.utility.Players

/**
  * Created by TheTemportalist on 1/17/2016.
  */
trait ICommand extends CommandBase {

	def getUsage: String

	override def getCommandUsage(sender: ICommandSender): String = this.getUsage

	final def wrongUsage(suffix: String = null): Unit =
		this.wrongUse(this.getUsage + (if (suffix == null) "" else "." + suffix))

	private def wrongUse(message: String): Unit = {
		throw new WrongUsageException(message)
	}

	final def getPlayerProfile(sender: ICommandSender, str: String,
			checkSender: Boolean = true): GameProfile = {
		val cache = Players.getCache
		cache.getGameProfileForUsername(if (str == null) "" else str) match {
			case profile: GameProfile => profile // 1) try to get the profile by name
			case _ =>
				try cache.getProfileByUUID(UUID.fromString(str)) // 2) try str as UUID
				catch {
					case e: Exception =>
						if (checkSender) {
							sender match {
								case player: EntityPlayer =>
									return player.getGameProfile // 3) get sending player
								case _ =>
							}
						}
						wrongUse("commands.icommand.player")
						null
				}
		}
	}

	final def asInt(str: String): Int = {
		CommandBase.parseInt(str)
	}

	final def canOpLevel(sender: ICommandSender, level: Int): Boolean =
		sender.canCommandSenderUseCommand(level, this.getCommandName)

	final def incorrectOp(sender: ICommandSender, level: Int): Unit = {
		sender.addChatMessage(new ChatComponentText("You are not opped at level " + level))
	}

	final def isBadOp(sender: ICommandSender, level: Int): Boolean = {
		if (!this.canOpLevel(sender, level)) {
			this.incorrectOp(sender, level)
			false
		} else true
	}

}
