package net.milanaleksic.mtscheckaccount

// (I) pokretanje MOCK procesiranja
//new MainProcessor(DataProvider:new MockInformationProvider()).start()


// (II) pokretanje realnog procesiranja sa modema
new MainProcessor(DataProvider:new ZTEMF622InformationProvider()).start()


