package net.milanaleksic.mtscheckaccount.data

import org.apache.commons.logging.*
import net.milanaleksic.mtscheckaccount.os.Locator
import groovy.util.slurpersupport.GPathResult

public abstract class InformationProvider {

    protected static Log log = LogFactory.getLog(InformationProvider.class)

    def Locator locator

    def GPathResult config

    public InformationProvider(GPathResult config) {
        this.config = config
    }

    def abstract provideInformation(Closure closure)

}