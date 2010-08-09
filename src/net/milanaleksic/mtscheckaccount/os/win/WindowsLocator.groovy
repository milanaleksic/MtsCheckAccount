package net.milanaleksic.mtscheckaccount.os.win

import net.milanaleksic.mtscheckaccount.os.Locator

import groovy.util.slurpersupport.GPathResult

public class WindowsLocator implements Locator {

    public String getModemLocation(GPathResult config) {
        def tool = new RegistryTool()

        def identifier = tool.extractValueOfRegistryKey(
                config.os.windows.descriptor.@location.text(),
                config.os.windows.descriptor.@key.text())

        return tool.extractValueOfRegistryKey(
                config.os.windows.identifier.@location.text().replace('{identifier}', identifier),
                config.os.windows.identifier.@key.text())
    }

    public String getDefaultModemLocation(GPathResult config) {
        return config.os.windows.port
    }

}
