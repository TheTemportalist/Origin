package com.temportalist.origin.api.common.utility

import java.io.InputStreamReader
import java.net.URL

import org.apache.commons.io.IOUtils

/**
 * Created by TheTemportalist on 9/6/2015.
 */
class ThreadFetchOnlineFile[U](private var siteURL: String,
		private val returnFunction: String => U) extends Thread {

	this.setName("Fetch Online File Thread")
	this.setDaemon(true)

	override def run(): Unit = {
		var fileAsString: String = null
		try {
			val fileIn = new InputStreamReader(new URL(this.siteURL).openStream())
			fileAsString = IOUtils.toString(fileIn)
			fileIn.close()
		}
		catch {
			case e: Exception => e.printStackTrace()
		}
		this.returnFunction(fileAsString)
	}

}

object ThreadFetchOnlineFile {

	def fetchResource(url: String): String = {
		if (url != null)
			new ThreadFetchOnlineFile(url, (fileString: String) => return fileString).start()
		null
	}

}
