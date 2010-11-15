package net.milanaleksic.mtscheckaccount.data.mock

import net.milanaleksic.mtscheckaccount.data.*
import groovy.util.slurpersupport.GPathResult

public class MockInformationProvider extends InformationProvider {

    public MockInformationProvider(GPathResult config) {
        super(config)
    }

    public def provideInformation(Closure closure) {
        closure 'Mozete ugasiti program'

        def testReceivedFromMts = """
+CUSD: 1,"Tarifni profil: Surf Classic
Stanje: 0.00
Preostali besplatan saobracaj:
Podaci (kb):11175740",15
"""

        closure(MTSExtract.instance.extract(testReceivedFromMts))
    }
}