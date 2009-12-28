package net.milanaleksic.mtscheckaccount.provider

import net.milanaleksic.mtscheckaccount.provider.zte.*
import net.milanaleksic.mtscheckaccount.provider.mock.*

public class ProviderFactory{

	public static InformationProvider createZTEMF622InformationProvider() {
		return new ZTEMF622InformationProvider()
	}
	
	public static InformationProvider createMockInformationProvider() {
		return new MockInformationProvider()
	}
	
}
