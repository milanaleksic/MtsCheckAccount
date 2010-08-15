package net.milanaleksic.mtscheckaccount.os

import groovy.util.slurpersupport.GPathResult
import org.apache.commons.logging.*

public abstract class Locator {

    protected static Log log = LogFactory.getLog(Locator.class)

    public static int MODEM_UNRECOGNIZED = -1
    public static int MODEM_ZTE_MF622 = 0
    public static int MODEM_HUAWEI_E1550 = 1

    public abstract int getRecognizedModem()

    public abstract String getModemLocation(GPathResult config)

    public abstract String getDefaultModemLocation(GPathResult config)
}
