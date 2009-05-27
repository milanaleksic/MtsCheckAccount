package net.milanaleksic.mtscheckaccount

import javax.swing.JOptionPane

public class MTSExtract {

  def extract(readFromData) {
    def result = new InformationBean()
    try {
	    [   Zaduzenje:(/Zaduzenje/),
	        Stanje:(/Stanje/),
	        UMrezi:(/U mrezi mts/),
	        VanMreze:(/Van mreze/),
	        Sms:(/Sms/),
	        Gprs:(/Gprs[(]KB[)]/)
	    ].each { informationBeanPropertyName, nameOfRowInData ->
	    	(readFromData =~ /$nameOfRowInData:\s*[\d.]+/).each { match ->
		        (match =~ /\s[\d.]+/).each { match2 ->
		          result."$informationBeanPropertyName" = match2  
		        }
	    	}
	    }
    } catch(Throwable t) {
    	JOptionPane.showMessageDialog(null, "($t)\n${t.getMessage() != null ? t.getMessage() : ''}", 'Greska', JOptionPane.ERROR_MESSAGE)
        t.printStackTrace()
    }
    return result
  }

}