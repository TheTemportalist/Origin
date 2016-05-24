package com.temportalist.origin.internal.server.command

import java.util
import java.util.{Iterator, ArrayList, List}

import com.temportalist.origin.api.common.utility.Teleport
import com.temportalist.origin.foundation.common.utility.Players
import com.temportalist.origin.internal.common.Origin
import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer

import scala.collection.JavaConversions

/**
 *
 *
 * @author TheTemportalist
 */
object CommandOrigin extends CommandBase {

	var aliases: util.List[String] = new util.ArrayList[String]()
	this.aliases.add("origin")

	override def getCommandName: String = this.aliases.get(0)

	override def getCommandAliases: util.List[_] = {
		this.aliases
	}

	override def getCommandUsage(sender: ICommandSender): String =
		"origin < tp < [x] [y] [z] <Dimension Name:_> > >|< tpr <Player Name:_> <radius> >|< set <player> <health|hunger|sat> <number> >|< setMaxHealth <player> <amount> >"

	override def processCommand(sender: ICommandSender, args: Array[String]): Unit = {
		if (args.length == 0) {
			return
		}

		val commandType: String = args(0)

		if (commandType.equals("tp")) {
			// origin tp ...
			sender match {
				case player1: EntityPlayer =>
					var player: EntityPlayer = player1

					if (args.length == 1) {
						Teleport.toCursorPosition(player, 20000.0D)
						return
					}

					// origin tp x      y z
					// origin tp x      y z dimName
					// origin tp player x y z
					// origin tp player x y z       dimName

					if (args.length < 4) {
						Players.message(player, "Not enough arguments")
						return
					}

					var hasAlternatePlayer: Boolean = false
					try {
						val potentialXCoord: Double = args(1).toDouble
					}
					catch {
						case e: NumberFormatException =>
							hasAlternatePlayer = true
					}

					var coordStartIndex: Int = 1
					if (hasAlternatePlayer) {
						coordStartIndex += 1
						player = Players.getPlayer(args(1))
					}

					var x: Double = 0.0D
					var y: Double = 0.0D
					var z: Double = 0.0D
					try {
						x = args(coordStartIndex + 0).toDouble
						y = args(coordStartIndex + 1).toDouble
						z = args(coordStartIndex + 2).toDouble
					}
					catch {
						case e: NumberFormatException =>
							Players.message(player1,
								"Something went terribly wrong...")
							return
					}

					if ((!hasAlternatePlayer && args.length > 4) ||
							(hasAlternatePlayer && args.length > 5)) {
						var dimName: String = ""
						for (i <- coordStartIndex + 3 until args.length) {
							dimName = (if (dimName.equals("")) "" else dimName + " ") + args(i)
						}

						var dimID: Int = 0
						try {
							dimID = dimName.toInt
						}
						catch {
							case e: Exception =>
								if (!Origin.dimensions.containsKey(dimName)) {
									Players.message(player1,
										"\"" + dimName + "\"" + " is not a valid dimension name!")
									return
								}
								else {
									dimID = Origin.dimensions.get(dimName)
								}
						}

						Teleport.toDimension(player, dimID)

					}

					Teleport.toPoint(player, x, y, z)

				case _ =>
			}
		}
		else if (commandType.equals("tpr")) {
			// origin tpr TheTemportalist 10
			// origin tpr 10
			// origin tpr TheTemportalist
			// origin tpr

			var player: EntityPlayer = null
			var radius: Int = 1000

			if (args.length == 3) {
				player = Players.getPlayer(args(1))
				radius =
						try {
							args(2).toInt
						}
						catch {
							case e: Exception => radius
						}
			}
			else if (args.length == 2) {
				radius =
						try {
							args(1).toInt
						}
						catch {
							case e: Exception =>
								player = Players.getPlayer(args(1))
								radius
						}
			}
			else sender match {
				case player1: EntityPlayer =>
					player = player1
				case _ => return
			}

			// todo Teleport clas object call

		}
		else if (commandType.equals("set") && args.length == 4) {
			val player: EntityPlayer = Players.getPlayer(args(1))
			val amount: Int =
				try {
					args(3).toInt
				}
				catch {
					case e: Exception => -1
				}
			if (amount > -1) {
				if (args(2).equals("maxHealth")) {
					player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(amount)
				}
				else if (args(2).equals("health")) {
					player.setHealth(amount)
				}
				else if (args(2).equals("hunger")) {
					player.getFoodStats.setFoodLevel(amount)
				}
				else if (args(2).equals("sat")) {
					player.getFoodStats.setFoodSaturationLevel(amount)
				}
			}
		}
		else if (commandType.equals("setMaxHealth")) {
			val player: EntityPlayer = Players.getPlayer(args(1))
			val amount: Int =
				try {
					args(2).toInt
				}
				catch {
					case e: Exception => -1
				}
			if (amount > 0) {
				player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(amount)
			}
		}

	}

	override def addTabCompletionOptions(sender: ICommandSender,
			args: Array[String]): util.List[_] = {
		if (args.length > 1 && args(0).equals("set"))
			this.getListOfStringsFromIterableMatchingLastWord(
				args, JavaConversions.collectionAsScalaIterable(
					util.Arrays.asList(MinecraftServer.getServer.getAllUsernames))
			)
		else null
	}

	def getListOfStringsFromIterableMatchingLastWord(args: Array[String], iter: Iterable[_]): util.List[_] = {
		val s = args(args.length - 1)
		val list = new util.ArrayList[String]()
		iter.foreach(f => {
			val str = f.toString
			if (CommandBase.doesStringStartWith(s, str)) list.add(str)
		})
		list
	}

}
