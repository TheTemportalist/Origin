package temportalist.origin.api.common.utility

import java.util.Random
import javax.vecmath.GMatrix

import org.lwjgl.util.Color

/**
 *
 *
 * @author TheTemportalist
 */
object MathFuncs {

	def getRandomBetweenBounds(min: Int, max: Int): Int =
		this.getRandomBetweenBounds(new Random, min, max)

	def getRandomBetweenBounds(rand: Random, min: Int, max: Int): Int = {
		rand.nextInt(Math.abs(max - min)) + min
	}

	def chance(percent: Int): Boolean = {
		new Random().nextInt(100) < percent
	}

	/**
	 * a < n < b
	 * @return
	 */
	def between(a: Double, n: Double, b: Double): Boolean = {
		a < n && n < b
	}

	/**
	 * a <= n <= b
	 * @return
	 */
	def between_eq(a: Double, n: Double, b: Double): Boolean = {
		this.between(a, n, b) || a == n || n == b
	}

	def bind(min: Double, n: Double, max: Double, default: Double): Double = {
		if (min < n && n < max)
			default
		else
			n
	}

	def bound(min: Double, n: Double, max: Double): Double = {
		if (n < min) min
		else if (n > max) max
		else n
	}

	def getColor(prefix: String, hexstring: String): Color = {
		val r_Start: Int = prefix.length
		val g_Start: Int = r_Start + 2
		val b_Start: Int = g_Start + 2
		new Color(
			Integer.parseInt(hexstring.substring(r_Start, r_Start + 2), 16),
			Integer.parseInt(hexstring.substring(g_Start, g_Start + 2), 16),
			Integer.parseInt(hexstring.substring(b_Start, b_Start + 2), 16)
		)
	}

	def distance(x1: Double, y1: Double, x2: Double, y2: Double): Double = {
		this.hypotenuse(x2 - x1, y2 - y1)
	}

	def hypotenuse(x: Double, y: Double): Double = {
		Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))
	}

	def round(value: Double, step: Double): Double = {
		val mult: Double = 1d / step
		Math.floor(value * mult) / mult
	}

	private class ExtMatrix(rows: Int, cols: Int) extends GMatrix(rows, cols) {

		def this(m: ExtMatrix) {
			this(m.getNumRow, m.getNumCol)
		}

		def transposeRet(m: GMatrix): ExtMatrix = {
			this.transpose(m)
			this
		}

		def mulTransposeLeftRet(a: GMatrix, b: GMatrix): ExtMatrix = {
			this.mulTransposeLeft(a, b)
			this
		}

		def invertRet: ExtMatrix = {
			this.invert()
			this
		}

		def mulRet(a: GMatrix, b: GMatrix): ExtMatrix = {
			this.mul(a, b)
			this
		}

	}

	/**
	  * Performs quadratic regression using the Least Squares Regression method.<br />
	  * Would not recommend using often, as it could create lag if performed many times a second.
	  * @param data A set of points
	  * @tparam T Any type of number, although Doubles are preferred.
	  *           Each number is converted to a double during calculation.
	  * @return Coefficients in a tuple of (a, b, c)
	  */
	def quadraticRegression[T <: AnyVal](data: Array[(T, T)]): (Double, Double, Double) = {
		// Function for converting passed data to doubles
		def getDouble(num: T): Double = {
			num match {
				case b: Byte => b.toDouble
				case s: Short => s.toDouble
				case i: Int => i.toDouble
				case l: Long => l.toDouble
				case f: Float => f.toDouble
				case d: Double => d
				case _ => 0D
			}
		}


		// Create the x matrix
		val xData = for (p <- data) yield p._1
		val matrix_X = new ExtMatrix(data.length, 3)
		for (i <- data.indices) {
			val x = getDouble(xData(i))
			matrix_X.setRow(i, Array[Double](1, x, Math.pow(x, 2)))
		}

		// Create the y matrix
		val yData = for (p <- data) yield getDouble(p._2)
		val matrix_Y = new ExtMatrix(data.length, 1)
		matrix_Y.setColumn(0, yData)

		val matrixX_rows = matrix_X.getNumRow
		val matrixX_cols = matrix_X.getNumCol

		// Perform regression
		// i = invert(transpose(matrixX) * matrixX)
		val xInvertMultTrans = new ExtMatrix(matrixX_cols, matrixX_cols).
				mulTransposeLeftRet(matrix_X, matrix_X).invertRet
		// j = i * transpose(matrixX)
		val invertedTimesTrans = new ExtMatrix(xInvertMultTrans.getNumRow, matrixX_rows).
				mulRet(xInvertMultTrans,
					new ExtMatrix(matrixX_cols, matrixX_rows).transposeRet(matrix_X))
		// c = j * matrixY
		val coeff = new ExtMatrix(invertedTimesTrans.getNumRow, matrix_Y.getNumCol).
				mulRet(invertedTimesTrans, matrix_Y)

		(coeff.getElement(2, 0), coeff.getElement(1, 0), coeff.getElement(0, 0))
	}


}
