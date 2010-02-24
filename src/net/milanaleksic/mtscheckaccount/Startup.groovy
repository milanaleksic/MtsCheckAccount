package net.milanaleksic.mtscheckaccount

import net.milanaleksic.mtscheckaccount.provider.*
import net.milanaleksic.mtscheckaccount.os.*

// (I) pokretanje MOCK procesiranja
new MainProcessor(
		DataProvider:ProviderFactory.createMockInformationProvider(),
		Locator:LocatorFactory.createLocator()
).start()


// (II) pokretanje realnog procesiranja sa modema
//new MainProcessor(
//		DataProvider:ProviderFactory.createZTEMF622InformationProvider(),
//		Locator:LocatorFactory.createLocator()
//).start()


