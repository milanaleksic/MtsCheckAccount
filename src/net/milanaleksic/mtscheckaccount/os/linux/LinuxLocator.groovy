package net.milanaleksic.mtscheckaccount.os.linux

import groovy.util.slurpersupport.GPathResult
import net.milanaleksic.mtscheckaccount.os.Locator
import org.apache.commons.logging.*

public class LinuxLocator extends Locator {

    public LinuxLocator(GPathResult config) {
        super(config)
    }

    //TODO: uzmi u obzir konfiguraciju (ime uredjaja za HAL pretragu)
    @Override protected String[] doGetModemLocationForDevice(GPathResult device) {
        def found = 'hal-find-by-capability --capability modem'.execute().text.trim()

        log.debug "Pronadjeni modem uredjaji:\n ${found}"

        log.debug "\nAnaliza uredjaja:"
        def udiForZTE = null
        found.eachLine() {
            def productId = "hal-get-property --udi ${it} --key info.product".execute().text.trim()
            log.debug "udi=[${it}] => productId=[${productId}]"
            if (productId.trim() == device.linux.@deviceId.text()) {
                udiForZTE = it
            }
        }
        if (udiForZTE == null)
            throw new IllegalStateException('Uredjaj nije pronajen putem HAL pretrage')
        log.debug "\nPronadjen je ZTE modem na identifikatoru: udi=[${udiForZTE}]"
        def device_file = "hal-get-property --udi ${udiForZTE} --key linux.device_file".execute().text.trim()
        log.debug "Konacno, lokacija modema je [${device_file}]"
        return device_file
    }

}
