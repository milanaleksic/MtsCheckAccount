package net.milanaleksic.mtscheckaccount.locator

import net.milanaleksic.mtscheckaccount.locator.win.*
import net.milanaleksic.mtscheckaccount.locator.linux.*

public class LocatorFactory{

	public static Locator createLocator() {
		def osname = System.getProperty("os.name").toLowerCase()
		println "os.name=${osname}"
		if (osname == "linux")
			return new LinuxLocator()
		else if (osname.indexOf("windows") != -1)
			return new WindowsLocator()
		else 
			throw new IllegalStateException("Operativni sistem (${osname}) nije podrzan :(")
	}
	
}
