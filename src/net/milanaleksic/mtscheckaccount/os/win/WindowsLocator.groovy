package net.milanaleksic.mtscheckaccount.os.win

import net.milanaleksic.mtscheckaccount.os.Locator
import groovy.util.slurpersupport.GPathResult
import com.ice.jni.registry.*

public class WindowsLocator extends Locator {

    public WindowsLocator(GPathResult config) {
        super(config)
    }

    @Override protected String[] doGetModemLocationForDevice(GPathResult device) {
        log.debug "doGetModemLocation - ${device.windows.@deviceId.toString()}"
        return getPortsForDevice(
                device.windows.@deviceId.toString(),
                device.windows.@deviceFriendlyNameRegEx.toString())
    }

    private String[] getPortsForDevice(String deviceId, String expectedFriendlyName) {
        log.debug "Open Reg key: SYSTEM\\CurrentControlSet\\Services\\${deviceId}\\Enum"
        try {
            RegistryKey serviceEnum = Registry.HKEY_LOCAL_MACHINE.openSubKey("SYSTEM\\CurrentControlSet\\Services\\${deviceId}\\Enum")
            RegDWordValue count = (RegDWordValue)serviceEnum.getValue("Count")
            log.debug "Maximum Count for device: $count.data"
            if (count.data==0)
                return
            def arr = (count.data-1..0).collect { int key ->
                return serviceEnum.getStringValue("$key")
            }
            log.debug "Available enums for the modem $deviceId: $arr"
            def ports = []
            arr.each { String deviceDefinition ->
                RegistryKey deviceKey = Registry.HKEY_LOCAL_MACHINE.openSubKey("SYSTEM\\CurrentControlSet\\Enum\\$deviceDefinition")
                String friendlyName = deviceKey.getStringValue("FriendlyName")
                if (!(friendlyName ==~ expectedFriendlyName))
                    return
                RegistryKey deviceParameters = deviceKey.openSubKey("Device Parameters")
                ports += deviceParameters.getStringValue('PortName')
            }
            log.debug "Ports: $ports"
            return ports
        } catch(com.ice.jni.registry.NoSuchKeyException e) {
            log.info "No registry key found when querying for $expectedFriendlyName"
            return []
        }
    }

}
