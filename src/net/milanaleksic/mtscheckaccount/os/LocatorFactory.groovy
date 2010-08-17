package net.milanaleksic.mtscheckaccount.os

import net.milanaleksic.mtscheckaccount.os.win.WindowsLocator
import net.milanaleksic.mtscheckaccount.os.linux.LinuxLocator
import net.milanaleksic.mtscheckaccount.os.mock.MockLocator
import org.apache.commons.logging.*

public class LocatorFactory {

    private static Log log = LogFactory.getLog(LocatorFactory.class)

    public static Locator fromConfig(config) {
        if (config.devices.@mock=="true")
            return LocatorFactory.createMockLocator(config)
        else
            return LocatorFactory.createOSLocator(config)
    }

    public static Locator createOSLocator(config) {
        def osname = System.getProperty("os.name").toLowerCase()
        log.debug "os.name=${osname}"
        if (osname == "linux")
            return new LinuxLocator(config)
        else if (osname.indexOf("windows") != -1)
            return new WindowsLocator(config)
        else
            throw new IllegalStateException("Operativni sistem (${osname}) nije podrzan :(")
    }

    public static Locator createMockLocator(config) {
        log.warn 'Using MOCK locator!'
        return new MockLocator(config)
    }

}
