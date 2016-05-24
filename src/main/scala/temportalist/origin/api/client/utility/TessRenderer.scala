package temportalist.origin.api.client.utility

import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.renderer.{Tessellator, WorldRenderer}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
object TessRenderer {

	def getTess: Tessellator = Tessellator.getInstance()

	def getRenderer: WorldRenderer = this.getTess.getWorldRenderer

	def begin(i: Int, format: VertexFormat): Unit = this.getRenderer.begin(i, format)

	def startQuads(format: VertexFormat): Unit = this.begin(GL11.GL_QUADS, format)

	def draw(): Unit = this.getTess.draw()

}
