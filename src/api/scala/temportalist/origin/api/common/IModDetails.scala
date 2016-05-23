package temportalist.origin.api.common

/**
  * Provides a reliable way to access the details of a mod
  *
  * Created by TheTemportalist on 4/9/2016.
  * @author TheTemportalist
  */
trait IModDetails {

	/**
	  *
	  * @return A mod's ID
	  */
	def getModId: String

	/**
	  *
	  * @return A mod's name
	  */
	def getModName: String

	/**
	  *
	  * @return A mod's version
	  */
	def getModVersion: String

}
