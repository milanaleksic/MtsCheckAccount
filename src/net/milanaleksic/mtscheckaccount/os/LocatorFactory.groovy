package net.milanaleksic.mtscheckaccount.os

import net.milanaleksic.mtscheckaccount.os.win.WindowsLocator
import net.milanaleksic.mtscheckaccount.os.linux.LinuxLocator
import net.milanaleksic.mtscheckaccount.os.mock.MockLocator
import org.apache.commons.logging.*

public class LocatorFactory {

    private static Log log = LogFactory.getLog(LocatorFactory.class)

    public static Locator fromConfig(config) {
        switch (config.core.os.toString()) {
            case "real":
                return LocatorFactory.createOSLocator()
            case "mock":
                return LocatorFactory.createMockLocator()
            default:
                throw new IllegalArgumentException("Nepoznat os - ${config.core.os.toString()}")
        }
    }

    public static Locator createOSLocator() {
        def osname = System.getProperty("os.name").toLowerCase()
        log.debug "os.name=${osname}"
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
