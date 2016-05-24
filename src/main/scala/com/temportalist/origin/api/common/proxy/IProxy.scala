package com.temportalist.origin.api.common.proxy

import com.temportalist.origin.api.common.register.Register
import cpw.mods.fml.common.network.IGuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
trait IProxy extends IGuiHandler with Register.Unusual {

	def postInit(): Unit = {}

	override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int): AnyRef = {
		this.getClientElement(
			ID, player, world, x, y, z, world.getTileEntity(x, y, z)
		)
	}

	override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int): AnyRef = {
		this.getServerElement(
			ID, player, world, x, y, z, world.getTileEntity(x, y, z)
		)
	}

	def getClientElement(ID: Int, player: EntityPlayer, world: World,
			x: Int, y: Int, z: Int, tileEntity: TileEntity): AnyRef

	def getServerElement(ID: Int, player: EntityPlayer, world: World,
			x: Int, y: Int, z: Int, tileEntity: TileEntity): AnyRef

}
