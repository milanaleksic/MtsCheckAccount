package net.milanaleksic.mtscheckaccount

import org.apache.commons.logging.*
import org.apache.log4j.xml.DOMConfigurator

DOMConfigurator.configure "log4j.xml"
try {
    MainProcessor.instance.start()
} catch(Throwable t) {
    LogFactory.getLog(this.class).error("Startup je uhvatio exception zbog kojeg ce se ugasiti", t)
}

