package net.milanaleksic.mtscheckaccount

import groovy.swing.SwingBuilder
import javax.swing.JOptionPane
import java.awt.Cursor
import java.awt.Desktop
import java.awt.Font

/**
 * @author Milan Aleksic
 *
 */
public class MainProcessor {

  InformationProvider DataProvider;

  def edZaduzenje, edStanje, edUMrezi, edVanMreze, edSms, edGprs

  def params() {
    def config=null
    def stream = MainProcessor.class.getResourceAsStream('config.xml')
    
    try {
      config = new XmlParser().parseText(stream.getText())
    }
    catch (Throwable t) {
      t.printStackTrace()
      println 'Greska - nisam uspeo da otvorim config.xml'
      System.exit(0)
    }

    def result = [:];
    config.each { result[it.name()] = it.text() }
    result.each { key, value -> println "Parametar ${key} je postavljen na ${value}" }
    
    // detaljna obrada za port, posto je on neophodan parametar...
    def autoResolvedPort = tryToResolvePort(result) 
    if (autoResolvedPort)
        result.port = autoResolvedPort
    else {
        def newPort = JOptionPane.showInputDialog(null, 
            'Na zalost, nisam bio u stanju da automatski saznam koji port koristi modem.\n'+
            'Procitajte uputstvo kako da dodjete do ove informacije pa je unesite ovde (npr. COM7):', 
            'Nepoznat port modema',
            JOptionPane.INFORMATION_MESSAGE)
        if (newPort)
            result.port = newPort
    }
    return result
  }
  
  def tryToResolvePort(config) {
	  def result = null
	  try {
		  def extractor = new RegistryExtractor()
	        
	      def identifier = extractor.extractValueOfRegistryKey(
	    		  config.'enum-descriptor-location', 
	    		  config.'enum-descriptor-key')
	        
	      result = extractor.extractValueOfRegistryKey(
	    		  config.'port-identifier-location'.replace('{identifier}',identifier), 
	    		  config.'port-identifier-key')
	      
	      if (result)
	    	  println "Uspesno je dovucena informacija o portu modema - port koristi $result"
	  } catch (Throwable t) {
		  t.printStackTrace()
	  }
	  return result
  }

  def provide(params, Closure closure) {
    if (!params)
      return null
    return DataProvider.provideInformation(params, closure)
  }

  def showForm() {
    def swing = new SwingBuilder()
    swing.lookAndFeel('com.sun.java.swing.plaf.windows.WindowsLookAndFeel')
    def frame = swing.frame(title:'ZTE MF622 3G Modem - mt:s CheckPostpaidAccount v0.2',
            location: [100,100],
            resizable: false,
            windowClosing : { event: System.exit(0) }) {
      panel {
        tableLayout {
          tr {
            td { label 'Zaduzenje: ' }
            td { edZaduzenje = textField (editable:false, text: "MOLIM, SACEKAJTE.......") }
          }
          tr {
            td { label 'Stanje: ' }
            td { edStanje = textField (editable:false, text: "MOLIM, SACEKAJTE.......") }
          }
          tr {
            td (colspan:2) { label ' ' }
          }
          tr {
            td (colspan:2) { label 'Preostali besplatni saobracaj:' }
          }
          tr {
            td () { label 'U mrezi mt:s: ' }
            td { edUMrezi = textField (editable:false, text: "MOLIM, SACEKAJTE.......") }
          }
          tr {
            td () { label 'Van mreze: ' }
            td { edVanMreze = textField (editable:false, text: "MOLIM, SACEKAJTE.......") }
          }
          tr {
            td () { label 'SMS: ' }
            td { edSms = textField (editable:false, text: "MOLIM, SACEKAJTE.......") }
          }
          tr {
            td () { label 'Gprs: ' }
            td { edGprs = textField (editable:false, text: "MOLIM, SACEKAJTE.......") }
          }
          tr {
            td (colspan:2) { label ' ' }
          }
          tr {
            td (colspan:2, align:'CENTER') { label('milanaleksic@gmail.com', 
            		font: new Font(null, Font.BOLD, 10),
            		cursor: Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
            		mouseClicked: {event: Desktop.getDesktop().mail('mailto:milanaleksic@gmail.com'.toURI())} ) }
	      }
          tr {
            td (colspan:2, align:'CENTER') { label('http://www.milanaleksic.net',
            		font: new Font(null, Font.BOLD, 10),
            		cursor: Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
            		mouseClicked: {event: Desktop.getDesktop().browse('http://www.milanaleksic.net'.toURI())} ) }
          }
        }
      }
    }
    frame.pack()
    frame.visible = true
  }

  def start() {
    try {
      showForm()
      provide ( params() ) { informationBean ->
        edZaduzenje.text = informationBean.Zaduzenje
        edStanje.text = informationBean.Stanje
        edUMrezi.text = informationBean.UMrezi
        edVanMreze.text = informationBean.VanMreze
        edSms.text = informationBean.Sms
        def gprstext = null 
        try {
        	def inMB = new BigDecimal(Long.parseLong(informationBean.Gprs) / 1024).setScale(2)
        	gprstext = "${informationBean.Gprs}KB (${inMB}MB)"
        } catch (Throwable t) {
        	gprstext = informationBean.Gprs;
        }
        edGprs.text = gprstext
      }
    } catch (RuntimeException t) {
      JOptionPane.showMessageDialog(null, "(${t.class})\n${t.getMessage() != null ? t.getMessage() : ''}", 'Greska', JOptionPane.ERROR_MESSAGE)
      t.printStackTrace()
      System.exit(1);
    } catch (Throwable t) {
      JOptionPane.showMessageDialog(null, "(${t.class})\n${t.getMessage() != null ? t.getMessage() : ''}", 'Greska', JOptionPane.ERROR_MESSAGE)
      t.printStackTrace()
      System.exit(1);
    }
  }

}

public class ThreadExcHandler implements Thread.UncaughtExceptionHandler {
  void uncaughtException(Thread thr, Throwable t) {
    JOptionPane.showMessageDialog(null, "[$thr]\n($t)\n${t.getMessage() != null ? t.getMessage() : ''}", 'Greska', JOptionPane.ERROR_MESSAGE)
      e.printStackTrace();
      System.exit(1);
  }
}