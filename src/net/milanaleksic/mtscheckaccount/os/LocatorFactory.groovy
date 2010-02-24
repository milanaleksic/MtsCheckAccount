package net.milanaleksic.mtscheckaccount.os

import net.milanaleksic.mtscheckaccount.os.win.*
import net.milanaleksic.mtscheckaccount.os.linux.*

public class LocatorFactory {

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
