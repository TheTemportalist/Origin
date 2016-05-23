package temportalist.origin.foundation.common.registers

import net.minecraftforge.fml.common.event.FMLStateEvent
import temportalist.origin.api.common.IModDetails

import scala.collection.mutable

/**
 *
 *
 * @author TheTemportalist
 */
trait Register {

	private var mod: IModDetails = null

	final def setMod(mod: IModDetails): Unit = {
		this.mod = mod
	}

	final def getMod: IModDetails = this.mod

	def priority: Int

	def getRegFuncType: Class[_ <: Register]

	def register(): Unit

}

object Register {

	trait Pre extends Register {
		override def priority: Int = 0
		override def getRegFuncType: Class[_ <: Register] = this.getClass
	}

	trait Post extends Register {
		override def priority: Int = 0
		override def getRegFuncType: Class[_ <: Register] = this.getClass
	}

	/**
	  * Will not function list a normal register. DO NOT USE FOR PRE INIT AUTOMATION
	  */
	trait Unusual extends Register {
		override final def priority: Int = -1
		override final def getRegFuncType: Class[_ <: Register] = classOf[Register.Unusual]
	}

	private val registerFunctions = mutable.Map[Class[_ <: Register],
			mutable.Map[RegisterPhase, (Register) => Unit]](
	            // Items
	            classOf[ItemRegister] ->
			            mutable.Map[RegisterPhase, (Register) => Unit](
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
			            mutable.Map[RegisterPhase, (Register) => Unit](
				            // Basic pre-init
				            RegisterPhase.PRE_INIT -> ((reg: Register) => {
					            // tiles
					            reg.asInstanceOf[BlockRegister].registerTileEntities()
					            // all blocks
					            reg.register()
					            reg match {
						            case blockReg: BlockRegister =>
							            // custom rendering handler
							            // todo Origin.proxy.registerBlockResources(blockReg)
						            case _ =>
					            }
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
			            mutable.Map[RegisterPhase, (Register) => Unit](
				            RegisterPhase.POST_ITEM -> ((reg: Register) => {
					            reg.register()
					            reg.asInstanceOf[EntityRegister].addEntitySpawns()
				            })
			            )
			)

	def addRegisterFunction(reg: Class[_ <: Register], phase: RegisterPhase,
			func: (Register) => Unit): Unit = {
		if (classOf[Register.Unusual].isAssignableFrom(reg)) return
		if (!this.registerFunctions.contains(reg))
			this.registerFunctions(reg) =
					mutable.Map[RegisterPhase, (Register) => Unit]()
		this.registerFunctions(reg)(phase) = func
	}

	def doRegistration(reg: Register, phase: RegisterPhase, details: IModDetails,
			event: FMLStateEvent): Unit = {
		if (reg.isInstanceOf[Register.Unusual]) return
		val regClassType = reg.getRegFuncType
		if (this.registerFunctions.contains(regClassType)) {
			val funcs = this.registerFunctions(regClassType)
			if (funcs.contains(phase)) funcs(phase).apply(reg)
		}
		else if (phase == RegisterPhase.INIT) reg.register()
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
