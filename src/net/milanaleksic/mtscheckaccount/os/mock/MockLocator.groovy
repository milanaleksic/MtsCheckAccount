package net.milanaleksic.mtscheckaccount.os.mock

import groovy.util.slurpersupport.GPathResult
import net.milanaleksic.mtscheckaccount.os.Locator

public class MockLocator extends Locator {

    @Override public String getModemLocation(GPathResult config) {
        def port = "MOCKED_PORT"
        log.debug "MOCKovana lokacija modema je [$port]"
        return port
    }

    @Override public String getDefaultModemLocation(GPathResult config) {
        return "MOCKED_PORT"
    }

    @Override int getRecognizedModem() {
        return MODEM_UNRECOGNIZED
    }
}
