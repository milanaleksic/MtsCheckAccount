package net.milanaleksic.mtscheckaccount.os.win

import net.milanaleksic.mtscheckaccount.os.Locator
import groovy.util.slurpersupport.GPathResult
import com.ice.jni.registry.*

public class WindowsLocator extends Locator {

    public WindowsLocator(GPathResult config) {
        super(config)
    }

    @Override protected String[] doGetModemLocationForDevice(GPathResult device) {
        return getPortsForDevice(
                device.windows.@deviceId.text(),
                device.windows.@deviceFriendlyNameRegEx.text())
    }

    private String[] getPortsForDevice(String deviceId, String expectedFriendlyName) {
        log.debug "Otvaram Registry lokaciju: SYSTEM\\CurrentControlSet\\Services\\${deviceId}\\Enum"
        try {
            RegistryKey serviceEnum = Registry.HKEY_LOCAL_MACHINE.openSubKey("SYSTEM\\CurrentControlSet\\Services\\${deviceId}\\Enum")
            RegDWordValue count = (RegDWordValue)serviceEnum.getValue("Count")
            log.debug "Max Count za uredjaj: $count.data"
            if (count.data==0)
                return
            def arr = (count.data-1..0).collect { int key ->
                return serviceEnum.getStringValue("$key")
            }
            log.debug "Moguci enum-i za modem $deviceId: $arr"
            def ports = []
            arr.each { String deviceDefinition ->
                RegistryKey deviceKey = Registry.HKEY_LOCAL_MACHINE.openSubKey("SYSTEM\\CurrentControlSet\\Enum\\$deviceDefinition")
                String friendlyName = deviceKey.getStringValue("FriendlyName")
                if (!(friendlyName ==~ expectedFriendlyName))
                    return
                RegistryKey deviceParameters = deviceKey.openSubKey("Device Parameters")
                ports += deviceParameters.getStringValue('PortName')
            }
            log.debug "Pronadjeni portovi: $ports"
            return ports
        } catch(com.ice.jni.registry.NoSuchKeyException e) {
            log.info "Kljuc nije mogao biti pronadjen u Registru za uredjaj $expectedFriendlyName"
            return []
        } catch(com.ice.jni.registry.NoSuchValueException e) {
            log.info "Value nije mogao biti pronadjen u Registru za uredjaj $expectedFriendlyName"
            return []
        }
    }

}
