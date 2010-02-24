package net.milanaleksic.mtscheckaccount.data

import net.milanaleksic.mtscheckaccount.data.zte.*
import net.milanaleksic.mtscheckaccount.data.mock.*

public class ProviderFactory{

	public static InformationProvider createZTEMF622InformationProvider() {
		return new ZTEMF622InformationProvider()
	}
	
	public static InformationProvider createMockInformationProvider() {
		return new MockInformationProvider()
	}
	
}
