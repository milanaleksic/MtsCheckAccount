package net.milanaleksic.mtscheckaccount.os.linux

import groovy.util.slurpersupport.GPathResult
import net.milanaleksic.mtscheckaccount.os.Locator
import org.apache.commons.logging.*

public class LinuxLocator extends Locator {

    @Override public String getModemLocation(GPathResult config) {
        def found = 'hal-find-by-capability --capability modem'.execute().text.trim()

        log.debug "Pronadjeni modem uredjaji:\n ${found}"

        log.debug "\nAnaliza uredjaja:"
        def udiForZTE = null
        found.eachLine() {
            def productId = "hal-get-property --udi ${it} --key info.product".execute().text.trim()
            log.debug "udi=[${it}] => productId=[${productId}]"
            if (productId.trim() == 'ZTE CDMA Technologies MSM') {
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

    @Override public String getDefaultModemLocation(GPathResult config) {
        return config.os.linux.port
    }

    @Override int getRecognizedModem() {
        throw new IllegalStateException("NYI")
    }
}
