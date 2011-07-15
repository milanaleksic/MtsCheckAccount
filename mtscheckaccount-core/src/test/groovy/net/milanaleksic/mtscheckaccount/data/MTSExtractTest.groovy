package net.milanaleksic.mtscheckaccount.data

class MTSExtractTest extends GroovyTestCase {

    def config
    def testSubject

    void setUp() {
        config = new XmlSlurper().parseText('''<?xml version="1.0" encoding="UTF-8"?><config><parser>
        <info id="Stanje" regex="Stanje" />
        <info id="UMrezi" regex="Minuti u mts" />
        <info id="VanMreze" regex="Minuti van mts" />
        <info id="Sms" regex="SMS" />
        <info id="Gprs" regex="Podaci\\s?[(](k|K)(b|B)[)]" />
    </parser></config>
''')
        testSubject = new MTSExtract(config)
    }

    void test_parser_current_valid_string() {
        def bean = testSubject.extract('''
+CUSD: 0,"Tarifni profil: Control_Mobilni_Net_3
Stanje: 0.00
Preostali besplatni saobracaj:
Podaci(kB): 3145728",15
''')
        assertTrue("Polje Stanje nije dobro - ${bean.Stanje}", bean.Stanje == "0.00")
        assertTrue("Polje UMrezi nije dobro - ${bean.UMrezi}", bean.UMrezi == "NEDOSTUPNO")
        assertTrue("Polje VanMreze nije dobro - ${bean.VanMreze}", bean.VanMreze == "NEDOSTUPNO")
        assertTrue("Polje Sms nije dobro - ${bean.Sms}", bean.Sms == "NEDOSTUPNO")
        assertTrue("Polje Gprs nije dobro - ${bean.Gprs}", bean.Gprs == "3145728")
    }

    void test_parser_current_surf_classic_string() {
        def bean = testSubject.extract('''
+CUSD: 0,"Tarifni profil: Surf Classic
Stanje: 3.12
Preostali besplatni saobracaj:
Podaci (kb): 12214240",15
''')
        assertTrue("Polje Stanje nije dobro - ${bean.Stanje}", bean.Stanje == "3.12")
        assertTrue("Polje UMrezi nije dobro - ${bean.UMrezi}", bean.UMrezi == "NEDOSTUPNO")
        assertTrue("Polje VanMreze nije dobro - ${bean.VanMreze}", bean.VanMreze == "NEDOSTUPNO")
        assertTrue("Polje Sms nije dobro - ${bean.Sms}", bean.Sms == "NEDOSTUPNO")
        assertTrue("Polje Gprs nije dobro - ${bean.Gprs}", bean.Gprs == "12214240")
    }

    void test_parser_current_invalid_string() {
        def bean = testSubject.extract('''
+CUSD: 0,"Tarifni profil: Control_Mobilni_Net_3
Stajne: 0.00
Preostali besplatni saobracaj:
Podatci(kB): 3145728",15
''')
        assertTrue("Polje Stanje nije dobro - ${bean.Stanje}", bean.Stanje == "NEDOSTUPNO")
        assertTrue("Polje UMrezi nije dobro - ${bean.UMrezi}", bean.UMrezi == "NEDOSTUPNO")
        assertTrue("Polje VanMreze nije dobro - ${bean.VanMreze}", bean.VanMreze == "NEDOSTUPNO")
        assertTrue("Polje Sms nije dobro - ${bean.Sms}", bean.Sms == "NEDOSTUPNO")
        assertTrue("Polje Gprs nije dobro - ${bean.Gprs}", bean.Gprs == "NEDOSTUPNO")
    }



}
