package net.milanaleksic.mtscheckaccount.data.mock

import net.milanaleksic.mtscheckaccount.data.*

public class MockInformationProvider implements InformationProvider {

    public def provideInformation(params, String port, Closure closure) {
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
+CUSD: 0,"Tarifni profil: Profesionalna tarifa II
Stanje: 125.56
Preostali besplatni saobracaj:
Minuti u mts: 156
Minuti van mts: 215
SMS: 99987
Podaci(kB): 3336380",15
"""

        closure(new MTSExtract().extract(testReceivedFromMts))
    }
}