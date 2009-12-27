package net.milanaleksic.mtscheckaccount.locator.win

import net.milanaleksic.mtscheckaccount.locator.Locator

public class WindowsLocator implements Locator{
	
	public String getModemLocation(){
		def tool = new RegistryTool()
        
	      def identifier = tool.extractValueOfRegistryKey(
	    		  config.descriptor.@location.text(), 
	    		  config.descriptor.@key.text())
	        
	      result = tool.extractValueOfRegistryKey(
	    		  config.identifier.@location.text().replace('{identifier}',identifier), 
	    		  config.identifier.@key.text())
	}
	
}
