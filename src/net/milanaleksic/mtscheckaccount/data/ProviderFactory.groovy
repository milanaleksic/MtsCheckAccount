package net.milanaleksic.mtscheckaccount.data

import net.milanaleksic.mtscheckaccount.data.zte.*
import net.milanaleksic.mtscheckaccount.data.mock.*

public class ProviderFactory{
	
	public static InformationProvider fromConfig(config) {
		switch (config.core.data.toString()) {
		  case "zte": 
			  return ProviderFactory.createZTEMF622InformationProvider()
		  case "mock":
			  return ProviderFactory.createMockInformationProvider()
		  default:
			  throw new IllegalArgumentException("Nepoznat data - ${config.core.data.toString()}")
		}
	}

	public static InformationProvider createZTEMF622InformationProvider() {
		return new ZTEMF622InformationProvider()
	}
	
	public static InformationProvider createMockInformationProvider() {
		return new MockInformationProvider()
	}
	
}
