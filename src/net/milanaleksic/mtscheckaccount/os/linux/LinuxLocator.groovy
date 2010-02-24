package net.milanaleksic.mtscheckaccount.os.linux

import groovy.util.slurpersupport.GPathResult
import net.milanaleksic.mtscheckaccount.os.Locator

public class LinuxLocator implements Locator {

	public String getModemLocation(GPathResult config) {
		def found = 'hal-find-by-capability --capability modem'.execute().text.trim()

		println "Pronadjeni modem uredjaji:\n ${found}"

		println "\nAnaliza uredjaja:"
		def udiForZTE = null
		found.eachLine() {
		    def productId = "hal-get-property --udi ${it} --key info.product".execute().text.trim()
		    println "udi=[${it}] => productId=[${productId}]"
		    if (productId.trim()=='ZTE CDMA Technologies MSM') {
		    	udiForZTE = it
		    }
		}
		if (udiForZTE == null) 
			throw new IllegalStateException('Uredjaj nije pronajen putem HAL pretrage')
		println "\nPronadjen je ZTE modem na identifikatoru: udi=[${udiForZTE}]"
	    def device_file = "hal-get-property --udi ${udiForZTE} --key linux.device_file".execute().text.trim()
	    println "Konacno, lokacija modema je [${device_file}]"
		return device_file
	}
	
	public String getDefaultModemLocation(GPathResult config) {
		return config.os.linux.port
	}

}
