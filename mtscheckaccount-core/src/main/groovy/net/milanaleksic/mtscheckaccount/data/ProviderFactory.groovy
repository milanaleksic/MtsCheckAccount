package net.milanaleksic.mtscheckaccount.data

import net.milanaleksic.mtscheckaccount.data.adaptive.*
import net.milanaleksic.mtscheckaccount.data.mock.*
import org.apache.commons.logging.*

@Singleton public class ProviderFactory {

    protected static Log log = LogFactory.getLog(ProviderFactory.class)

    public InformationProvider fromConfig(config) {
        if (config.data.@mock == "true")
            return createMockInformationProvider(config)
        else
            return createZTEMF622InformationProvider(config)
    }

    private InformationProvider createZTEMF622InformationProvider(config) {
        return new AdaptiveInformationProvider(config)
    }

    private InformationProvider createMockInformationProvider(config) {
        log.warn 'Using MOCK information provider!'
        return new MockInformationProvider(config)
    }

}
