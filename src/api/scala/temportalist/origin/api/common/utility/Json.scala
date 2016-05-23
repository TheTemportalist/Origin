package temportalist.origin.api.common.utility

import java.io.{File, FileReader, Reader}
import java.util
import java.util.Map.Entry

import com.google.common.io.Files
import com.google.gson._
import net.minecraft.item.ItemStack
import net.minecraft.nbt._
import net.minecraftforge.common.config.Property
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.origin.api.common.helper.Names

/**
 *
 *
 * @author TheTemportalist
 */
object Json {

	val gson: Gson = new Gson()
	val parser: JsonParser = new JsonParser()

	def toReadableString(json: String): String = {
		var readable: String = ""
		val chars: Array[Char] = json.toCharArray
		var isIteratingInString: Boolean = false
		var tabs: Int = 0
		for (c <- chars) {
			if (c == '}' || c == ']') {
				tabs -= 1
				readable = this.addLineAndTabs(readable, tabs)
			}

			readable += c

			if (c == '{' || c == '[') {
				tabs += 1
				readable = this.addLineAndTabs(readable, tabs)
			}

			if (c == ':' && !isIteratingInString) {
				readable += " "
			}

			if (c == '"') {
				isIteratingInString = !isIteratingInString
			}

			if (c == ',' && !isIteratingInString) {
				readable = this.addLineAndTabs(readable, tabs)
			}

		}

		readable
	}

	private def addLineAndTabs(currentString: String, tabs: Int): String = {
		var str: String = currentString
		str += '\n'
		for (i <- 1 to tabs) {
			str += "   "
		}
		str
	}

	def registerRecipe(array: JsonArray): Unit = {
		var result: ItemStack = null
		for (i <- 0 until array.size()) {
			val parts: JsonArray = array.get(i).getAsJsonArray
			result = Names.getItemStack(parts.get(0).getAsString)
			var isShaped: Boolean = false
			val objs: Array[Object] = new Array[Object](parts.size - 1)
			for (i1 <- 1 until parts.size()) {
				val j: Int = i1 - 1
				val comp: String = parts.get(i1).getAsString
				if (comp.matches("(.*):(.*)")) {
					objs(j) = Names.getItemStack(comp)
				}
				else if (comp.length == 1 && j > 1) {
					objs(j) = comp.charAt(0) + ""
				}
				else {
					objs(j) = comp
					isShaped = true
				}
			}
			if (isShaped) {
				GameRegistry.addShapedRecipe(result, objs)
			}
			else {
				GameRegistry.addShapelessRecipe(result, objs)
			}
		}
	}

	def writeToFile(jsonElement: JsonElement, file: File, formatted: Boolean): Unit = {
		val jsonStr = this.gson.toJson(jsonElement)
		val fileStr = if (formatted) this.toReadableString(jsonStr) else jsonStr
		Files.write(fileStr.getBytes, file)
	}

	def getJson(file: File): JsonElement = this.parser.parse(new FileReader(file))

	def getJson(reader: Reader): JsonElement = this.parser.parse(reader)

	def jsonToNBT(file: File): NBTBase = {
		this.jsonToNBT(this.getJson(file))
	}

	val primitivePostfixes: List[Char] = List[Char]('B', 'S', 'I', 'L', 'F', 'D')

	def jsonToNBT(json: JsonElement): NBTBase = {
		var ret: NBTBase = null
		json match {
			case prim: JsonPrimitive =>
				if (prim.isBoolean)
					ret = new NBTTagByte(if (prim.getAsBoolean) 1 else 0)
				else {
					val value: String = prim.getAsString
					val valueType: Char = value.last
					if (this.primitivePostfixes.contains(valueType)) {
						try {
							val number: Double = value.substring(0, value.length - 1).toDouble
							if (number.toByte == number)
								ret = new NBTTagByte(number.toByte)
							else if (number.toShort == number)
								ret = new NBTTagShort(number.toShort)
							else if (number.toInt == number)
								ret = new NBTTagInt(number.toInt)
							else if (number.toLong == number)
								ret = new NBTTagLong(number.toLong)
							else if (number.toFloat == number)
								ret = new NBTTagFloat(number.toFloat)
							else ret = new NBTTagDouble(number)
						}
						catch {
							case e: NumberFormatException =>
						}
					}
					if (ret == null)
						ret = new NBTTagString(value)
				}
			case array: JsonArray =>

				/**
				 * 1 = Byte Array
				 * 2 = Int Array
				 * 3 = Tag List
				 */
				val localRetType: Byte =
					if (array.size() > 0) {
						val arrayType: Byte = this.jsonToNBT(array.get(0)).getId
						if (arrayType == 1) 1
						else if (arrayType == 3) 2
						else 3
					}
					else 3
				val data =
					if (localRetType == 1) new Array[Byte](array.size())
					else if (localRetType == 2) new Array[Int](array.size())
					else new NBTTagList
				Scala.foreach(array, (index: Int, element: JsonElement) => {
					if (localRetType == 1)
						data.asInstanceOf[Array[Byte]](index) =
								this.jsonToNBT(element).asInstanceOf[NBTTagByte].getByte
					else if (localRetType == 2)
						data.asInstanceOf[Array[Int]](index) =
								this.jsonToNBT(element).asInstanceOf[NBTTagInt].getInt
					else data.asInstanceOf[NBTTagList].appendTag(this.jsonToNBT(element))
				})
				ret = if (localRetType == 1) new NBTTagByteArray(data.asInstanceOf[Array[Byte]])
				else if (localRetType == 2) new NBTTagIntArray(data.asInstanceOf[Array[Int]])
				else data.asInstanceOf[NBTTagList]
			case obj: JsonObject =>
				val tag: NBTTagCompound = new NBTTagCompound
				Scala.iterateCol(obj.entrySet(), (entry: Entry[String, JsonElement]) => {
					tag.setTag(entry.getKey, this.jsonToNBT(entry.getValue))
				})
				ret = tag
			case _ =>
		}
		ret
	}

	def nbtToJson(nbt: NBTBase): JsonElement = {
		nbt match {
			case b: NBTTagByte => this.objectToJson(b.getByte)
			case s: NBTTagShort => this.objectToJson(s.getShort)
			case i: NBTTagInt => this.objectToJson(i.getInt)
			case l: NBTTagLong => this.objectToJson(l.getLong)
			case f: NBTTagFloat => this.objectToJson(f.getFloat)
			case d: NBTTagDouble => this.objectToJson(d.getDouble)
			case ab: NBTTagByteArray => this.objectToJson(ab.getByteArray)
			case s: NBTTagString => this.objectToJson(s.getString)
			case list: NBTTagList =>
				val array: JsonArray = new JsonArray
				Scala.foreach(list, (index: Int, any: Any) => {
					array.add(this.nbtToJson(any.asInstanceOf[NBTBase]))
				}: Unit)
				array
			case comp: NBTTagCompound =>
				val json: JsonObject = new JsonObject
				Scala.foreach(comp, (key: String, nbt: NBTBase) => {
					json.add(key, this.nbtToJson(nbt))
				}: Unit)
				json
			case ai: NBTTagIntArray => this.objectToJson(ai.getIntArray)
			case _ => null
		}
	}

	def objectToJson(any: Any): JsonElement = {
		any match {
			case b: Byte => this.objectToJson(b.toString + this.primitivePostfixes.head)
			case s: Short => this.objectToJson(s.toString + this.primitivePostfixes(1))
			case i: Int => this.objectToJson(i.toString + this.primitivePostfixes(2))
			case l: Long => this.objectToJson(l.toString + this.primitivePostfixes(3))
			case f: Float => this.objectToJson(f.toString + this.primitivePostfixes(4))
			case d: Double => this.objectToJson(d.toString + this.primitivePostfixes(5))
			case ab: Array[Byte] =>
				val array: JsonArray = new JsonArray
				for (b: Byte <- ab) array.add(this.objectToJson(b))
				array
			case s: String => new JsonPrimitive(s)
			case ai: Array[Int] =>
				val array: JsonArray = new JsonArray
				for (i: Int <- ai) array.add(this.objectToJson(i))
				array
			case _ => null
		}
	}

	object Config {

		def addTo(json: util.HashMap[String, Any], comments: util.HashMap[String, AnyRef]): Unit = {
			json.put("Bool", false)
			json.put("Integer", 20)
			comments.put("Integer", "This int comment")
			json.put("Float", 1.0F)
			comments.put("Float", Array[String]("Line 1", "Line 2", "Line 3"))
			json.put("Double", 2.0D)
			json.put("String", "strgda")
			json.put("array", Array[Int](0, 5, 2))
			val map: util.HashMap[Int, Int] = new util.HashMap[Int, Int]()
			map.put(0, -1)
			map.put(1, 1)
			map.put(3, 4)
			json.put("list", map)

		}

		def toJson(obj: Any): JsonElement = {
			obj match {
				case bool: Boolean =>
					new JsonPrimitive(bool)
				case int: Int =>
					new JsonPrimitive(int)
				case float: Float =>
					new JsonPrimitive(float)
				case double: Double =>
					new JsonPrimitive(double)
				case string: String =>
					new JsonPrimitive(string)
				case ar: Array[_] =>
					val jsonArray: JsonArray = new JsonArray()
					for (i <- ar.indices) {
						jsonArray.add(this.toJson(ar(i)))
					}
					jsonArray
				case map: util.Map[_, _] =>
					val jsonObj: JsonObject = new JsonObject()
					val iter: util.Iterator[_] = map.keySet().iterator()
					while (iter.hasNext) {
						val key = iter.next()
						jsonObj.add(key.toString, this.toJson(map.get(key)))
					}
					jsonObj
				case prop: Property =>
					prop.getType match {
						case Property.Type.BOOLEAN =>
							if (prop.isList) this.toJson(prop.getBooleanList)
							else this.toJson(prop.getBoolean)
						case Property.Type.DOUBLE =>
							if (prop.isList) this.toJson(prop.getDoubleList)
							else this.toJson(prop.getDouble)
						case Property.Type.INTEGER =>
							if (prop.isList) this.toJson(prop.getIntList)
							else this.toJson(prop.getInt)
						case Property.Type.STRING =>
							if (prop.isList) this.toJson(prop.getStringList)
							else this.toJson(prop.getString)
						case _ =>
							null
					}
				case element: JsonElement => element
				case _ =>
					null
			}
		}

		def toString(element: JsonElement, tab: Int): String = {
			this.toString(element, new util.HashMap[String, String](), tab)
		}

		def toString(element: JsonElement, comments: util.Map[String, _], tab: Int): String = {
			var indents: Int = tab
			element match {
				case prim: JsonPrimitive =>
					if (prim.isBoolean) prim.getAsBoolean + ""
					else if (prim.isNumber) prim.getAsNumber + ""
					else "\"" + prim.getAsString + "\""
				case array: JsonArray =>
					val sb: StringBuilder = new StringBuilder("[\n")
					indents += 1
					for (i <- 0 until array.size()) {
						this.tab(sb, indents)
						sb.append(this.toString(array.get(i), comments, indents))
						if (i != array.size() - 1)
							sb.append(",")
						this.line(sb)
					}
					indents -= 1
					this.tab(sb, indents)
					sb.append("]")
					sb.toString()
				case obj: JsonObject =>
					val sb: StringBuilder = new StringBuilder("{\n")
					indents += 1
					val iter: util.Iterator[Entry[String, JsonElement]] = obj.entrySet().iterator()
					while (iter.hasNext) {
						val entry: Entry[String, JsonElement] = iter.next()
						val name: String = entry.getKey

						if (comments.containsKey(name)) {
							comments.get(name) match {
								case str: String =>
									this.tab(sb, indents)
									sb.append("// " + str)
								case ar: Array[String] =>
									this.tab(sb, indents)
									sb.append("/*")
									this.line(sb)
									indents += 1
									for (i <- ar.indices) {
										this.tab(sb, indents)
										sb.append(ar(i))
										this.line(sb)
									}
									indents -= 1
									this.tab(sb, indents)
									sb.append("*/")
								case _ =>
							}
							this.line(sb)
						}

						val value: JsonElement = entry.getValue
						this.tab(sb, indents)
						sb.append("\"" + name + "\": ")
						sb.append(this.toString(value, comments, indents))
						if (iter.hasNext) sb.append(",")
						this.line(sb)

					}
					indents -= 1
					this.tab(sb, indents)
					sb.append("}")
					sb.toString()
				case _ =>
					""
			}
		}

		def line(builder: StringBuilder): Unit = builder.append("\n")

		def tab(builder: StringBuilder, length: Int): Unit =
			for (i <- 1 to length) builder.append("\t")

	}

}
