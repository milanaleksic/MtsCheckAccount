package net.milanaleksic.mtscheckaccount.data.mock

import net.milanaleksic.mtscheckaccount.data.*

public class MockInformationProvider extends InformationProvider {

    public def provideInformation(params, Closure closure) {
        closure 'Mozete ugasiti program'

        assert """
+CUSD: 1,"mt:s vodic
Stanje: nedostupno
Izaberite opciju i potvrdite.

1. Prepaid dopuna
2. Stanje racuna
3. Dodatne usluge
4. Zabava i info
",15
""" =~ params.data.pre[2].@response

        def testReceivedFromMts = """
+CUSD: 1,"Tarifni profil: Surf Classic
Stanje: 0.00
Preostali besplatan saobracaj:
Podaci (kb):11175740",15
"""

        closure(new MTSExtract().extract(testReceivedFromMts))
    }
}