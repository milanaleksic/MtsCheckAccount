package net.milanaleksic.mtscheckaccount.os

import net.milanaleksic.mtscheckaccount.os.win.WindowsLocator
import net.milanaleksic.mtscheckaccount.os.linux.LinuxLocator
import net.milanaleksic.mtscheckaccount.os.mock.MockLocator
import org.apache.commons.logging.*

@Singleton public class LocatorFactory {

    private static Log log = LogFactory.getLog(LocatorFactory.class)

    public Locator fromConfig(config) {
        if (config.devices.@mock=="true")
            return createMockLocator(config)
        else
            return createOSLocator(config)
    }

    private Locator createOSLocator(config) {
        def osname = System.getProperty("os.name").toLowerCase()
        log.debug "os.name=${osname}"
        if (osname == "linux")
            return new LinuxLocator(config)
        else if (osname.indexOf("windows") != -1)
            return new WindowsLocator(config)
        else
            throw new IllegalStateException("Operativni sistem (${osname}) nije podrzan :(")
    }

    private Locator createMockLocator(config) {
        log.warn 'Using MOCK locator!'
        return new MockLocator(config)
    }

}
