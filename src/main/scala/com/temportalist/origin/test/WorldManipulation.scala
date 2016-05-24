package com.temportalist.origin.test

import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.foundation.common.utility.Players
import com.temportalist.origin.internal.common.handlers.RegisterHelper
import net.minecraft.block.{BlockFalling, Block}
import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.MathHelper
import net.minecraft.world.World

/**
 * Created by TheTemportalist on 10/12/2015.
 */
object WorldManipulation {

	def preInit(): Unit = RegisterHelper.registerCommand(Command)

	object Command extends CommandBase {

		val maxDepth = 45
		val minDepth = 5
		final val SEMI: Int = 0
		final val CONE: Int = 1
		final val DOME: Int = 2
		final val BOX: Int = 3
		final val TUBE: Int = 4
		final val SPHERE: Int = 5

		override def getCommandName: String = "worldmanip"

		override def getCommandUsage(sender: ICommandSender): String = {
			"command.worldmanip.usage"
		}

		override def processCommand(sender: ICommandSender, args: Array[String]): Unit = {
			sender match {
				case player: EntityPlayer =>
					/*
					lift | remove
						smooth - pos degree(1-10) radius=10 hAbove=10
							semi    1
							cone    5
							cavern  10+
						box - pos whl hAbove = 10
						tube - pos height radius=10 hAbove=10
					params:
						player | x y z

					worldmanip lift smooth <degree> < playerName | x y z > [radius=10] [heightAbove=10] [moveHeight=30]
					worldmanip
						lift
							smooth
								degree
								position
								[radius]
								[heightAbove]
								[moveHeight]
							semi
								position
								[radius]
								[heightAbove]
								[moveHeight]
							cone
								position
								[radius]
								[heightAbove]
								[moveHeight]
							cavern
								position
								[radius]
								[heightAbove]
								[moveHeight]
							box
								width
								height
								length
								position
								[heightAbove]
								[moveHeight]
							tube
								height
								position
								[radius]
								[heightAbove]
								[moveHeight]
					 */
					if (args.length < 2) {
						this.chat(player, "Not enough information.")
						return
					}
					var function: String = null
					var shape: String = null
					var shapeInt: Int = -1
					var degree: Float = 0
					var position: (Int, Int, Int) = null
					var wlh: (Int, Int, Int) = (0, 0, 0)
					var diameter: Int = 10
					var heightAbove: Int = 10
					var moveHeight: Int = 30
					var nextParamIndex = 0
					try {
						function = args(0).toLowerCase
						shape = args(1)

						shape match {
							case "semi" =>
								degree = 0
								shapeInt = SEMI
								nextParamIndex = 2
							case "cone" =>
								degree = 5
								shapeInt = CONE
								nextParamIndex = 2
							case "cavern" =>
								degree = 10
								shapeInt = DOME
								nextParamIndex = 2
							case "sphere" =>
								shapeInt = SPHERE
								nextParamIndex = 2
							case "box" =>
								wlh = (args(2).toInt, args(3).toInt, args(4).toInt)
								shapeInt = BOX
								nextParamIndex = 5
							case "tube" =>
								wlh = (0, 0, args(2).toInt)
								shapeInt = TUBE
								nextParamIndex = 3
							case _ =>
								this.chat(player, "No shape of type \"" + shape + "\".")
						}

						val pos = this.getPosition(player, args, nextParamIndex)
						if (pos._1 == null) {
							this.chat(player, "The position was invalid.")
							return
						}
						position = pos._1
						nextParamIndex = pos._2

						if (!shape.equals("box") && args.length >= nextParamIndex + 1) {
							diameter = args(nextParamIndex).toInt
							wlh = (if (wlh._1 <= 0) diameter else wlh._1,
									if (wlh._2 <= 0) diameter else wlh._2,
									if (wlh._3 <= 0) diameter else wlh._3)
							nextParamIndex += 1
						}
						if (args.length >= nextParamIndex + 1) {
							heightAbove = args(nextParamIndex).toInt
							nextParamIndex += 1
						}
						if (args.length >= nextParamIndex + 1) {
							moveHeight = args(nextParamIndex).toInt
							nextParamIndex += 1
						}

						this.manipulate(function, shapeInt, position,
							wlh, diameter, heightAbove, moveHeight, player)

					}
					catch {
						case e: Exception =>
							e.printStackTrace()
							this.chat(player, "There was an error in your syntax.")
					}
				case _ =>
			}
		}

		def chat(p: EntityPlayer, output: String): Unit = {
			Players.message(p, output)
		}

		def getPosition(sender: EntityPlayer,
				args: Array[String], startIndex: Int): ((Int, Int, Int), Int) = {
			var pos: (Int, Int, Int) = null
			var nextIndex = startIndex
			try {
				val xyz = new Array[Int](3)
				var pointStr: String = null
				for (i <- 0 to 2) {
					pointStr = args(startIndex + i)
					xyz(i) =
							if (pointStr.startsWith("~")) pointStr.substring(1).toInt
							else pointStr.toInt
				}
				pos = (xyz(0), xyz(1), xyz(2))
				nextIndex += 3
			}
			catch {
				case e: Exception =>
					Players.getPlayer(args(startIndex)) match {
						case player: EntityPlayer =>
							pos = (MathHelper.floor_double(player.posX),
									MathHelper.floor_double(player.posY),
									MathHelper.floor_double(player.posZ))
							nextIndex += 1
						case _ =>
					}
			}
			(pos, nextIndex)
		}

		def manipulate(function: String, shape: Int, position: (Int, Int, Int),
				wlh: (Int, Int, Int), diameter: Int, heightAbove: Int, moveHeight: Int,
				sender: EntityPlayer): Unit = {

			val removing = function.startsWith("remove")
			val lifting = function.startsWith("lift")
			val genning = function.startsWith("gen")
			val isHollow = function.endsWith("hollow")
			//val removing = function.equals("remove")

			val world = sender.getEntityWorld
			val pos = new V3O(position._1, position._2, position._3)
			val up = V3O.UP.up(moveHeight)

			shape match {
				/*
			case this.SEMI =>
				val move = 30
				Origin.log("Generating shape " + shape + "\nradius = " + radius + "\nmove = " + moveHeight)
				Sphere.generateSphere(6, 6, 6, 0, (x: Int, z: Int, y: Int) => {
					sender.getEntityWorld.setBlock(
						x + position._1, y + position._2 + move, z + position._3,
						Blocks.stone, 0, 3)
				})
				*/
				case this.DOME =>
					Dome.generateDome(diameter, diameter, heightAbove, isHollow,
						(x: Int, z: Int, y: Int) =>
							moveBlock(world, x, z, y, pos, up, removing, genning)
					)
				case this.BOX =>
					Box.generateBox(wlh._1, wlh._2, wlh._3, heightAbove, isHollow,
						(x: Int, z: Int, y: Int) => {
							moveBlock(world, x, z, y, pos, up, removing, genning)
						}
					)
					/* todo broken
				case this.SPHERE =>
					Sphere.generateSphere(diameter, heightAbove, isHollow,
						(x: Int, z: Int, y: Int) => {
							moveBlock(world, x, z, y, pos, up, removing, genning)
						}
					)
					*/
				case _ =>
			}

		}

		private def moveBlock(world: World, x: Int, z: Int, y: Int, pos: V3O, up: V3O,
				removing: Boolean, genning: Boolean): Unit = {
			val origin = new V3O(x, y, z) + pos
			if (removing) origin.setBlockToAir(world)
			else this.moveBlock(world, origin, origin + up, !genning)
		}

		private def moveBlock(world: World, origin: V3O, target: V3O,
				shouldRemoveOriginal: Boolean): Unit = {
			val block = origin.getBlock(world)
			println(origin.toString + "-" + block.getUnlocalizedName)
			if (this.canMoveBlock(block, world, origin)) {
				println("to " + target.toString)
				val id = Block.getIdFromBlock(block)
				val meta = origin.getBlockMeta(world)
				val tileOrigin = origin.getTile(world)
				val tileTagCompound: NBTTagCompound = new NBTTagCompound
				var tileTarget: TileEntity = null
				if (tileOrigin != null) {
					tileTarget = block.createTileEntity(world, meta)
					tileOrigin.writeToNBT(tileTagCompound)
					world.setTileEntity(origin.x_i(), origin.y_i(), origin.z_i(), tileTarget)
				}
				if (shouldRemoveOriginal) origin.setBlockToAir(world)
				//world.playAuxSFX(2001, origin.x_i(), origin.y_i(), origin.z_i(), id + (meta << 12))

				if (tileTagCompound.hasKey("id")) {
					tileTarget = TileEntity.createAndLoadEntity(tileTagCompound)
				}

				target.setBlock(world, block, meta, 3) //1 | 2)
				if (tileTarget != null) {
					tileTarget.xCoord = target.x_i()
					tileTarget.yCoord = target.y_i()
					tileTarget.zCoord = target.z_i()
					world.setTileEntity(target.x_i(), target.y_i(), target.z_i(), tileTarget)
				}

			}

		}

		private def canMoveBlock(block: Block, world: World, pos: V3O): Boolean = {
			!(/*pos.isAir(world) || */ pos.isReplaceable(world) ||
					block.isInstanceOf[BlockFalling] || pos.getHardness(world) == -1)
		}

	}

	abstract class Shape {

		var width: Int = 0
		var length: Int = 0
		var height: Int = 0
		var isHollow: Boolean = false
		var xRadius: Double = 0
		var yRadius: Double = 0
		var xRadius_i: Int = 0
		var yRadius_i: Int = 0
		var centerVertical: (Double, Double) = null

		protected def generateShape[U](heightAbove: Int, f: (Int, Int, Int) => U): Unit

		protected def generateObject[U](width: Int, length: Int, height: Int, heightAbove: Int,
				isHollow: Boolean, f: (Int, Int, Int) => U): Unit = {
			this.width = width
			this.length = length
			this.height = height
			this.isHollow = isHollow
			this.xRadius = width / 2D
			this.yRadius = length / 2D
			this.xRadius_i = this.xRadius.toInt
			this.yRadius_i = this.yRadius.toInt
			this.centerVertical = (this.xRadius - 0.5, this.yRadius - 0.5)
			this.generateShape(heightAbove, f)
		}

		private def checkDistance(distance: Double, localRadius: Double,
				isFaceLayer: Boolean): Boolean = {
			if (isHollow)
				distance < localRadius && (isFaceLayer || localRadius - 1 < distance)
			else
				distance < localRadius
		}

		/**
		 * Generates XXX, where the x and y axis are horizontal (x is width, y is length)
		 * param f A function which is called for each possible vertice (passed [x, y])
		 */
		/*
		final def generateCircle[U](diameter: Int, f: (Int, Int) => U): Unit = {
			this.generateCircle((diameter, diameter), f)
		}

		private final def generateCircle[U](wl: (Int, Int), f: (Int, Int) => U): Unit = {
			val xRadius = wl._1 / 2D
			val yRadius = wl._2 / 2D
			val minRadius = Math.min(xRadius, yRadius)
			val center = (xRadius - 0.5, yRadius - 0.5)
			for (x <- 0 until wl._1) {
				for (y <- 0 until wl._2) {
					// Distance Forumla: sqrt( (x_2 - x_1)^2 + (y_2 - y_1)^2 )
					val distance = Math.sqrt(
						Math.pow(center._1 - x, 2) + Math.pow(center._2 - y, 2)
					)
					val distanceX = Math.sqrt(
						Math.pow(center._1 - x, 2) + Math.pow(center._2 - center._2, 2)
					)
					val distanceY = Math.sqrt(
						Math.pow(center._1 - center._1, 2) + Math.pow(center._2 - y, 2)
					)
					val isInRadius =
						(distanceX < distanceY && distance <= yRadius) ||
								(distanceY < distanceX && distance <= xRadius) ||
								(distance <= minRadius)
					if (isInRadius) f(x, y)
					//println(x + "\t" + y + "\t" + distance + "\t" + isInRadius)
				}
				//println()
			}
		}
		*/

		final protected def generateCircle[U](localRadius: Double, isFaceLayer: Boolean,
				f: (Int, Int) => U): Unit = {
			for (x <- 0 until this.width) {
				for (y <- 0 until this.length) {
					// Distance Forumla: sqrt( (x_2 - x_1)^2 + (y_2 - y_1)^2 )
					val distance = Math.sqrt(
						Math.pow(this.centerVertical._1 - x, 2)
								+ Math.pow(this.centerVertical._2 - y, 2)
					)
					if (this.checkDistance(distance, localRadius, isFaceLayer))
						f(x - this.xRadius_i, y - this.yRadius_i)
				}
			}
		}

	}

	object Dome extends Shape {

		private var isDome: Boolean = false

		def generateDome[U](diameter: Int, height: Int, heightAbove: Int, domeVsCavern: Boolean,
				isHollow: Boolean, f: (Int, Int, Int) => U): Unit = {
			this.isDome = domeVsCavern
			this.generateObject(diameter, diameter, height, heightAbove, isHollow, f)
		}

		def generateDome[U](diameter: Int, height: Int, heightAbove: Int, isHollow: Boolean,
				f: (Int, Int, Int) => U): Unit = {
			this.generateDome(diameter, height, heightAbove, domeVsCavern = false, isHollow, f)
		}

		override protected def generateShape[U](heightAbove: Int, f: (Int, Int, Int) => U): Unit = {
			val zRadius = this.height
			val diameter = Math.min(this.width, this.length)
			val radius: Double = diameter / 2D
			val radius2: Double = (diameter - 1) / 2D

			var z_d: Double = 0
			var adjustedZ: Double = 0
			val zFactor: Double = zRadius / radius2
			val radiusSq: Double = Math.pow(radius, 2)
			var layerRadius: Double = 0
			for (z_layer <- 0 until zRadius) {
				z_d = z_layer + 0.5
				if (!this.isDome) z_d = zRadius - z_d
				adjustedZ = z_d / zFactor
				layerRadius = Math.sqrt(radiusSq - Math.pow(adjustedZ, 2))
				this.generateCircle(layerRadius, z_layer == 0 || z_layer == zRadius - 1,
					(x: Int, y: Int) => f(x, y, z_layer - zRadius))
			}
			for (z_layer <- 0 until heightAbove) {
				this.generateCircle(radius, true, (x: Int, y: Int) => f(x, y, z_layer))
			}
		}

	}

	object Box extends Shape {

		def generateBox[U](width: Int, length: Int, height: Int, heightAbove: Int,
				isHollow: Boolean, f: (Int, Int, Int) => U): Unit = {
			this.generateObject(width, length, height, heightAbove, isHollow, f)
		}

		override protected def generateShape[U](heightAbove: Int,
				f: (Int, Int, Int) => U): Unit = {
			val halfW: Int = this.width / 2
			val halfL: Int = this.length / 2
			for (z <- 0 until this.height + heightAbove) for (x <- 0 until this.width)
				for (y <- 0 until this.length)
					if (!isHollow || !(z > 0 && z < this.height - 1 &&
							x > 0 && x < this.width - 1 &&
							y > 0 && y < this.length - 1))
						f(x - halfW, y - halfL, z - this.height)
		}

	}

	object Sphere extends Shape {

		def generateSphere[U](diameter: Int, heightAbove: Int, isHollow: Boolean,
				f: (Int, Int, Int) => U): Unit = {
			this.generateObject(diameter, diameter, diameter, heightAbove, isHollow, f)
		}

		override protected def generateShape[U](heightAbove: Int,
				f: (Int, Int, Int) => U): Unit = {
			val zDiameter = this.height
			val zRadius = zDiameter / 2
			val diameter = Math.min(this.width, this.length)
			val radius: Double = diameter / 2D
			val radius2: Double = (diameter - 1) / 2D

			var z_d: Double = 0
			var adjustedZ: Double = 0
			val zFactor: Double = zDiameter / radius2
			val radiusSq: Double = Math.pow(radius, 2)
			var layerRadius: Double = 0
			for (z_layer <- 0 until zDiameter) {
				z_d = z_layer + 0.5
				//z_d = Math.abs(z_d - zRadius)
				adjustedZ = z_d / zFactor
				layerRadius = Math.sqrt(radiusSq - Math.pow(adjustedZ, 2))
				this.generateCircle(layerRadius, z_layer == 0 || z_layer == zDiameter - 1,
					(x: Int, y: Int) => f(x, y, z_layer - zDiameter))
			}

		}

	}

}
