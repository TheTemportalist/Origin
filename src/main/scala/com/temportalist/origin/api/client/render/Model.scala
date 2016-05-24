package com.temportalist.origin.api.client.render

import com.temportalist.origin.api.common.lib.V3O
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.model.{ModelBase, ModelRenderer}
import net.minecraft.entity.Entity
import net.minecraft.tileentity.TileEntity

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class Model(texWidth: Int, texHeight: Int) extends ModelBase {

	this.textureWidth = texWidth
	this.textureHeight = texHeight

	/**
	 *
	 * @param model The model
	 * @param rot Rotation around xyz axis' in degrees
	 */
	def setRotation(model: ModelRenderer, rot: V3O): Unit = {
		model.rotateAngleX = Math.toRadians(rot.x).toFloat
		model.rotateAngleY = Math.toRadians(rot.y).toFloat
		model.rotateAngleZ = Math.toRadians(rot.z).toFloat
	}

	override def render(entity: Entity, parTime: Float, parSwingSuppress: Float, unknown1: Float,
			headAngleY: Float, headAngleX: Float, unknown2: Float): Unit = {
		this.setRotationAngles(
			parTime, parSwingSuppress, unknown1, headAngleY, headAngleX, unknown2, entity
		)
		this.renderModel(unknown2)
	}

	def render(te: TileEntity): Unit = {
		this.setRotationAngles(0, 0, 0, 0, 0, Model.f5, null.asInstanceOf[Entity])
		this.renderModel(Model.f5)
	}

	def renderModel(f5: Float): Unit = {}

	override def setRotationAngles(parTime: Float, parSwingSuppress: Float, unknown1: Float,
			headAngleY: Float, headAngleX: Float, unknown2: Float, entity: Entity): Unit = {}

	protected def add(parent: ModelRenderer, child: ModelRenderer): Unit = {
		parent.addChild(child)
	}

	protected def createModel(origin: V3O, offset: V3O,
			bounds: V3O, rot: V3O, u: Int, v: Int): ModelRenderer = {
		val mr: ModelRenderer = new ModelRenderer(this, u, v)
		mr.setRotationPoint(origin.x_f(), origin.y_f(), origin.z_f())
		mr.addBox(
			-offset.x_f(), -offset.y_f(), -offset.z_f(),
			bounds.x_i(), bounds.y_i(), bounds.z_i()
		)
		mr.setTextureSize(this.textureWidth, this.textureHeight)
		mr.rotateAngleX = Math.toRadians(rot.x).asInstanceOf[Float]
		mr.rotateAngleY = Math.toRadians(rot.y).asInstanceOf[Float]
		mr.rotateAngleZ = Math.toRadians(rot.z).asInstanceOf[Float]
		mr
	}

}

@SideOnly(Side.CLIENT)
object Model {

	val f5: Float = 0.0625F

}
