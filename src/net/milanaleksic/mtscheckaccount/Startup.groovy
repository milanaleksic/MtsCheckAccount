package net.milanaleksic.mtscheckaccount

import net.milanaleksic.mtscheckaccount.provider.*
import net.milanaleksic.mtscheckaccount.provider.zte.*
import net.milanaleksic.mtscheckaccount.provider.mock.*
import net.milanaleksic.mtscheckaccount.locator.win.*

// (I) pokretanje MOCK procesiranja
//new MainProcessor(
//		DataProvider:new MockInformationProvider(),
//		Locator:new WindowsLocator()
//).start()


// (II) pokretanje realnog procesiranja sa modema
new MainProcessor(
		DataProvider:new ZTEMF622InformationProvider(),
		Locator:new WindowsLocator()
).start()


