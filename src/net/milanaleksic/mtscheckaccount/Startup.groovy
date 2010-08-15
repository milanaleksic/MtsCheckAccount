package net.milanaleksic.mtscheckaccount

import org.apache.log4j.xml.DOMConfigurator

DOMConfigurator.configure "log4j.xml"
new MainProcessor().start()
