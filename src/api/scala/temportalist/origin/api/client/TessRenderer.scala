package temportalist.origin.api.client

import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.renderer.{Tessellator, VertexBuffer}
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

	def getBuffer: VertexBuffer = this.getTess.getBuffer

	def begin(i: Int, format: VertexFormat): Unit = this.getBuffer.begin(i, format)

	def startQuads(format: VertexFormat): Unit = this.begin(GL11.GL_QUADS, format)

	def draw(): Unit = this.getTess.draw()

}
