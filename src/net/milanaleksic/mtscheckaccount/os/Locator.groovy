package net.milanaleksic.mtscheckaccount.os

import groovy.util.slurpersupport.GPathResult

public interface Locator {

	public String getModemLocation(GPathResult config)
	
	public String getDefaultModemLocation(GPathResult config)
}
