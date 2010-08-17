package net.milanaleksic.mtscheckaccount.data

import net.milanaleksic.mtscheckaccount.data.adaptive.*
import net.milanaleksic.mtscheckaccount.data.mock.*
import org.apache.commons.logging.*

public class ProviderFactory {

    protected static Log log = LogFactory.getLog(ProviderFactory.class)

    public static InformationProvider fromConfig(config) {
        if (config.data.@mock == "true")
            return ProviderFactory.createMockInformationProvider(config)
        else
            return ProviderFactory.createZTEMF622InformationProvider(config)
    }

    public static InformationProvider createZTEMF622InformationProvider(config) {
        return new AdaptiveInformationProvider(config)
    }

    public static InformationProvider createMockInformationProvider(config) {
        log.warn 'Using MOCK information provider!'
        return new MockInformationProvider(config)
    }

}
