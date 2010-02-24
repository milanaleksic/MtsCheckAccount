package net.milanaleksic.mtscheckaccount.os

import net.milanaleksic.mtscheckaccount.os.win.WindowsLocator
import net.milanaleksic.mtscheckaccount.os.linux.LinuxLocator
import net.milanaleksic.mtscheckaccount.os.mock.MockLocator

public class LocatorFactory {

	public static Locator createOSLocator() {
		def osname = System.getProperty("os.name").toLowerCase()
		println "os.name=${osname}"
		if (osname == "linux")
			return new LinuxLocator()
		else if (osname.indexOf("windows") != -1)
			return new WindowsLocator()
		else 
			throw new IllegalStateException("Operativni sistem (${osname}) nije podrzan :(")
	}
	
	public static Locator createMockLocator() {
		return new MockLocator()
	}
	
}
