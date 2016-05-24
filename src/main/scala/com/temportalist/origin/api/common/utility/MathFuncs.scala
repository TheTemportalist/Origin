package com.temportalist.origin.api.common.utility

import java.util.Random

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

}
