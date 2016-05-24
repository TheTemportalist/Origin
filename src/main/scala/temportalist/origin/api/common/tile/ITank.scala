package temportalist.origin.api.common.tile

import java.util

import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids._

/**
 *
 *
 * @author TheTemportalist
 */
trait ITank extends TileEntity with IFluidHandler {

	private val tanks: util.ArrayList[IFluidTank] = new util.ArrayList[IFluidTank]()

	protected def addTank(tank: IFluidTank): Unit = {
		this.tanks.add(tank)
	}

	protected def addTank(capacity: Int): Unit = this.addTank(new FluidTank(capacity))

	protected def addTank(fluidStack: FluidStack, capacity: Int): Unit =
		this.addTank(new FluidTank(fluidStack, capacity))

	protected def addTank(fluid: Fluid, amount: Int, capacity: Int): Unit =
		this.addTank(new FluidTank(new FluidStack(fluid, amount), capacity))

	def getTankForDirection(fluid: Fluid, from: EnumFacing): IFluidTank = null

	/**
	 *
	 * @param fluid The fluid the tank should contain. 'null' means any fluid.
	 * @return
	 */
	def getTank(fluid: Fluid, from: EnumFacing): IFluidTank = {
		val facingTank: IFluidTank = this.getTankForDirection(fluid, from)
		if (facingTank != null && (fluid == null || facingTank.getFluid.getFluid == fluid))
			return facingTank

		val iter: util.Iterator[IFluidTank] = this.tanks.iterator()
		var tank: IFluidTank = null
		while (iter.hasNext) {
			tank = iter.next()
			if (tank == null) iter.remove()
			else if (fluid == null || tank.getFluid.getFluid == fluid)
				return tank
		}
		null
	}

	def hasTank(fluid: Fluid, from: EnumFacing): Boolean = this.getTank(fluid, from) != null

	def clearTanks(): Unit = {
		for (i <- 0 until this.tanks.size())
			this.tanks.set(i, new FluidTank(this.tanks.get(i).getCapacity))
	}

	override def canFill(from: EnumFacing, fluid: Fluid): Boolean = {
		val tank: IFluidTank = this.getTank(fluid, from)
		tank.getFluidAmount < tank.getCapacity
	}

	override def canDrain(from: EnumFacing, fluid: Fluid): Boolean = {
		val tank: IFluidTank = this.getTank(fluid, from)
		tank.getFluidAmount > 0
	}

	override def fill(from: EnumFacing, resource: FluidStack, doFill: Boolean): Int = {
		val tank: IFluidTank = this.getTank(resource.getFluid, from)
		if (tank != null) {
			val amount: Int = tank.fill(resource, doFill)
			if (amount > 0 && doFill)
				this.updateTile()
			amount
		}
		else
			0
	}

	override def drain(from: EnumFacing, maxDrain: Int, doDrain: Boolean): FluidStack = {
		if (this.hasTank(null, from)) {
			this.drain(from, new FluidStack(null.asInstanceOf[Fluid], maxDrain), doDrain)
		}
		else null
	}

	override def drain(from: EnumFacing, resource: FluidStack, doDrain: Boolean): FluidStack = {
		val tank: IFluidTank = this.getTank(resource.getFluid, from)
		if (tank != null) {
			val stack: FluidStack = tank.drain(resource.amount, doDrain)
			if (stack != null && doDrain)
				this.updateTile()
			stack
		}
		else null
	}

	def updateTile(): Unit

	override def getTankInfo(from: EnumFacing): Array[FluidTankInfo] = {
		val info: Array[FluidTankInfo] = new Array[FluidTankInfo](this.tanks.size())
		for (i <- 0 until this.tanks.size()) {
			info(i) = this.tanks.get(i).getInfo
		}
		info
	}

	def writeNBT_ITank(tagCom: NBTTagCompound): Unit = {
		val tankList: NBTTagList = new NBTTagList
		for (i <- 0 until this.tanks.size()) {
			this.tanks.get(i) match {
				case tank: FluidTank =>
					val tag: NBTTagCompound = new NBTTagCompound
					tag.setInteger("capacity", this.tanks.get(i).getCapacity)
					tag.setTag("tank", tank.writeToNBT(new NBTTagCompound))
					tankList.appendTag(tag)
				case _ =>
			}
		}
		tagCom.setTag("tanks", tankList)
	}

	def fromNBT_ITank(tagCom: NBTTagCompound): Unit = {
		val tankList: NBTTagList = tagCom.getTagList("tanks", 10)
		for (i <- 0 until tankList.tagCount()) {
			val tag: NBTTagCompound = tankList.getCompoundTagAt(i)
			val tank: NBTTagCompound = tag.getCompoundTag("tank")
			this.tanks.add(new FluidTank(
				if (tank.hasKey("Empty")) null.asInstanceOf[FluidStack]
				else FluidStack.loadFluidStackFromNBT(tank),
				tag.getInteger("capacity"))
			)
		}
	}

}
