package net.milanaleksic.mtscheckaccount

// (I) pokretanje MOCK procesiranja
//new MainProcessor(DataProvider:new MockInformationProvider()).start()


// (II) pokretanje realnog procesiranja sa modema
new MainProcessor(DataProvider:new ZTEMF622InformationProvider()).start()


// (III) testiranje ekstrakcije nad (ranije primljenim) testnim baferom

//def testStr ="""
//+CUSD: 0,"Zaduzenje: 0.00
//Stanje: 1.70
//Preostali besplatni saobracaj:
//U mrezi mts: 0
//Van mreze: 0
//Sms: 0
//Gprs(KB): 908580 ",15
//"""
//
//println new MTSExtract().extract(testStr).dump()