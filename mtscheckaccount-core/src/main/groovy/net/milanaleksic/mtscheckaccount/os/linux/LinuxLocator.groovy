package net.milanaleksic.mtscheckaccount.os.linux

import groovy.util.slurpersupport.GPathResult
import net.milanaleksic.mtscheckaccount.os.Locator

public class LinuxLocator extends Locator {

    public LinuxLocator(GPathResult config) {
        super(config)
    }

    @Override protected String[] doGetModemLocationForDevice(GPathResult device) {
        return doGetModemLocationForDeviceId(
                device.linux.@port.text(),
                device.linux.@udi.text(),
                device.linux.@deviceId.text()
        )
    }

    private String[] doGetModemLocationForDeviceId(String port, String udi, String deviceId) {
        if (port) {
            log.debug "Koristim eksplicitno postavljen port [$port]"
            return [port]
        }

        def udiForModem = null
        if (udi) {
            log.debug "Koristim eksplicitno postavljen UDI [$udi]"
            udiForModem = udi
        } else {
            def found = 'hal-find-by-capability --capability modem'.execute().text.trim()

            log.debug "Pronadjeni modem uredjaji:\n ${found}"

            log.debug "\nAnaliza uredjaja:"

            udiForModem = null
            found.eachLine() {
                def productId = "hal-get-property --udi ${it} --key info.product".execute().text.trim()
                log.debug "udi=[${it}] => productId=[${productId}]"
                if (productId.trim() == deviceId) {
                    udiForModem = it
                }
            }

            if (udiForModem == null)
                return null
            log.debug "\nPronadjen je modem na identifikatoru: udi=[${udiForModem}]"
        }

        def device_file = "hal-get-property --udi ${udiForModem} --key linux.device_file".execute().text.trim()
        log.debug "Lokacija modema je [${device_file}]"
        return [device_file]
    }

}
