package com.temportalist.origin.api.common.register

import com.temportalist.origin.api.common.resource.IModDetails
import com.temportalist.origin.foundation.common.register.{BlockRegister, EntityRegister, ItemRegister, OptionRegister}
import com.temportalist.origin.internal.common.handlers.OptionHandler
import cpw.mods.fml.common.event.{FMLPreInitializationEvent, FMLStateEvent}

import scala.collection.mutable

/**
 *
 *
 * @author TheTemportalist
 */
trait Register {

	def priority: Int

	def getRegFuncType: Class[_ <: Register]

	def register(): Unit

}

object Register {

	trait Pre extends Register {}

	trait Post extends Register {}

	trait Unusual extends Register {
		override final def priority: Int = -1
		override final def getRegFuncType: Class[_ <: Register] = classOf[Register.Unusual]
	}

	private val registerFunctions = mutable.Map[Class[_ <: Register],
			mutable.Map[RegisterPhase, Function1[Register, Unit]]](
	            // Items
	            classOf[ItemRegister] ->
			            mutable.Map[RegisterPhase, Function1[Register, Unit]](
				            // Basic pre-init
				            RegisterPhase.PRE_INIT -> ((reg: Register) =>
					            reg.register()
						            ),
				            // Post block things
				            RegisterPhase.POST_BLOCK -> ((reg: Register) =>
					            reg.asInstanceOf[ItemRegister].registerItemsPostBlock()
						            ),
				            // init phase
				            RegisterPhase.INIT -> ((reg: Register) => reg match {
					            case r: ItemRegister =>
						            // crafting things
						            r.registerCrafting()
						            r.registerSmelting()
						            r.registerOther()
					            case _ =>
				            })
			            ),
	            // Blocks
	            classOf[BlockRegister] ->
			            mutable.Map[RegisterPhase, Function1[Register, Unit]](
				            // Basic pre-init
				            RegisterPhase.PRE_INIT -> ((reg: Register) => {
					            // tiles
					            reg.asInstanceOf[BlockRegister].registerTileEntities()
					            // all blocks
					            reg.register()
				            }),
				            // init phase
				            RegisterPhase.INIT -> ((reg: Register) => reg match {
					            case r: BlockRegister =>
						            // crafting things
						            r.registerCrafting()
						            r.registerSmelting()
						            r.registerOther()
					            case _ =>
				            })
			            ),
	            classOf[EntityRegister] ->
			            mutable.Map[RegisterPhase, Function1[Register, Unit]](
				            RegisterPhase.POST_ITEM -> ((reg: Register) => {
					            reg.register()
					            reg.asInstanceOf[EntityRegister].addEntitySpawns()
				            })
			            )
			)

	def addRegisterFunction(reg: Class[_ <: Register], phase: RegisterPhase,
			func: Function1[Register, Unit]): Unit = {
		if (classOf[Register.Unusual].isAssignableFrom(reg)) return
		if (!this.registerFunctions.contains(reg))
			this.registerFunctions(reg) =
					mutable.Map[RegisterPhase, Function1[Register, Unit]]()
		this.registerFunctions(reg)(phase) = func
	}

	def doRegistration(reg: Register, phase: RegisterPhase, details: IModDetails,
			event: FMLStateEvent): Unit = {
		if (reg.isInstanceOf[Register.Unusual]) return
		reg match {
			case r: OptionRegister =>
				OptionHandler.handleConfiguration(details, r,
					event.asInstanceOf[FMLPreInitializationEvent])
			case _ =>
				val regClassType = reg.getRegFuncType
				if (this.registerFunctions.contains(regClassType)) {
					val funcs = this.registerFunctions(regClassType)
					if (funcs.contains(phase)) funcs(phase).apply(reg)
				}
		}
	}

	object Order extends Ordering[Register] {
		override def compare(x: Register, y: Register): Int = {
			// before (-1)
			// same (0)
			// after (1)
			x match {
				case pre1: Register.Pre => y match {
					case pre2: Register.Pre =>
						if (x.priority > y.priority) -1
						else if (x.priority < y.priority) 1
						else 0
					case _ => 1
				}
				case post1: Register.Post => y match {
					case post2: Register.Post =>
						if (x.priority > y.priority) -1
						else if (x.priority < y.priority) 1
						else 0
					case _ => -1
				}
				case r1: Register => y match {
					case pre2: Register.Pre => -1
					case post2: Register.Post => 1
					case _ =>
						if (x.priority > y.priority) -1
						else if (x.priority < y.priority) 1
						else 0
				}
				case _ => 1
			}
		}
	}

}
