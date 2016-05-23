package temportalist.origin.internal.server

import java.util

import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import temportalist.origin.api.common.IModDetails
import temportalist.origin.foundation.server.Command
import temportalist.origin.internal.common.Origin

/**
  *
  * Created by TheTemportalist on 4/10/2016.
  *
  * @author TheTemportalist
  */
object CommandPlayerData extends Command {

	override def getDetails: IModDetails = Origin

	override def getCommandName: String = "playerdata"

	override def getCommandAliases: util.List[String] = {
		val aliases = new util.ArrayList[String]()
		aliases.add("pdata")
		aliases
	}

	override def execute(server: MinecraftServer, sender: ICommandSender,
			args: Array[String]): Unit = {
		if (args.length < 3) {
			this.wrongUsage()
			return
		}

		args(0) match {
			case "set" =>
				if (args.length < 4){
					this.wrongUsage()
					return
				}
				val player = server.getPlayerList.getPlayerByUsername(args(2))
				val value = CommandBase.parseDouble(args(3))
				args(1) match {
					case "health" => player.setHealth(value.toFloat)
					case "maxHealth" =>
						player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(value)
					case "hunger" => player.getFoodStats.setFoodLevel(value.toInt)
					case "saturation" => player.getFoodStats.setFoodSaturationLevel(value.toFloat)
					case _ =>
				}
			case "get" =>
				val player = server.getPlayerList.getPlayerByUsername(args(2))
				val value: Float =
					args(1) match {
						case "health" => player.getHealth
						case "maxHealth" => player.getMaxHealth
						case "hunger" => player.getFoodStats.getFoodLevel
						case "saturation" => player.getFoodStats.getSaturationLevel
						case _ => 0f
					}
				player.addChatMessage(new TextComponentString(value.toString))
			case _ =>
		}

	}

	override def getTabCompletionOptions(server: MinecraftServer, sender: ICommandSender,
			args: Array[String], pos: BlockPos): util.List[String] = {
		args.length match {
			case 1 =>
				CommandBase.getListOfStringsMatchingLastWord(args, "set", "get")
			case 2 =>
				CommandBase.getListOfStringsMatchingLastWord(args, "health", "maxHealth", "hunger", "saturation")
			case 3 =>
				CommandBase.getListOfStringsMatchingLastWord(args, server.getPlayerList.getAllUsernames:_*)
			case _ =>
				super.getTabCompletionOptions(server, sender, args, pos)
		}
	}
}
