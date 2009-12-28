package net.milanaleksic.mtscheckaccount.locator.win

import net.milanaleksic.mtscheckaccount.locator.Locator

import groovy.util.slurpersupport.GPathResult

public class WindowsLocator implements Locator{
	
	public String getModemLocation(GPathResult config){
		def tool = new RegistryTool()
        
	      def identifier = tool.extractValueOfRegistryKey(
	    		  config.windows.descriptor.@location.text(), 
	    		  config.windows.descriptor.@key.text())
	        
	      return tool.extractValueOfRegistryKey(
	    		  config.windows.identifier.@location.text().replace('{identifier}',identifier), 
	    		  config.windows.identifier.@key.text())
	}
	
	public String getDefaultModemLocation(GPathResult config) {
		return config.windows.port
	}
	
}
