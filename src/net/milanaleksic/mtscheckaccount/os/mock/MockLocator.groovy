package net.milanaleksic.mtscheckaccount.os.mock

import groovy.util.slurpersupport.GPathResult
import net.milanaleksic.mtscheckaccount.os.Locator

public class MockLocator implements Locator {

	public String getModemLocation(GPathResult config) {
		def port = "MOCKED_PORT"
	    println "MOCKovana lokacija modema je [$port]"
		return port
	}
	
	public String getDefaultModemLocation(GPathResult config) {
		return "MOCKED_PORT"
	}

}
