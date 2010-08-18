package net.milanaleksic.mtscheckaccount.os

import groovy.util.slurpersupport.GPathResult
import org.apache.commons.logging.*

public abstract class Locator {

    def recognizedModemId
    def recognizedModemPort

    public Locator(GPathResult config) {
        findModemLocations(config)
    }

    protected static Log log = LogFactory.getLog(Locator.class)

    protected abstract String[] doGetModemLocationForDevice(GPathResult device)

    private void findModemLocations(GPathResult config) {
        def recognizedModems = new HashMap<String,String[]>()
        config.devices.device.each { device ->
            def ports = doGetModemLocationForDevice(device)
            if (ports && (ports.size()>0)) {
                recognizedModems[device.@id.toString()] = ports
            }
        }
        log.info "Prepoznate lokacije modema: $recognizedModems"
        validateFoundModems(recognizedModems)
        recognizedModems.each { entry ->
            recognizedModemId = entry.key
            recognizedModemPort = entry.value[0]
        }
    }

    private def validateFoundModems(def recognizedModems) {
        if (recognizedModems == null || recognizedModems.size() != 1) {
            throw new IllegalStateException("Validacija lociranih modema je pala jer nije pronadjen nijedan (poznat) modem")
        }
        recognizedModems.each { entry ->
            if (entry.value.size() != 1)
                throw new IllegalStateException("Validacija lociranih modema je pala jer je pronadjeno vise od jednog porta na zakacenom modemu ${entry.key}")
        }
        log.debug "Validacija lociranih modema je prosla"
    }

}
