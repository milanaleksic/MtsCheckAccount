package net.milanaleksic.mtscheckaccount.os.mock

import groovy.util.slurpersupport.GPathResult
import net.milanaleksic.mtscheckaccount.os.Locator

public class MockLocator extends Locator {

    public MockLocator(GPathResult config) {
        super(config)
    }

    @Override protected String[] doGetModemLocationForDevice(GPathResult device) {
        def port = "MOCKED_PORT"
        log.debug "MOCKovana lokacija modema je [$port]"
        return [port]
    }

}
