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
  def edStatus

  def params() {
    def config=null
    try {
      config = new XmlSlurper().parse('config.xml')
    }
    catch (Throwable t) {
      t.printStackTrace()
      println 'Greska - nisam uspeo da otvorim konfiguraciju'
      System.exit(0)
    }

    // detaljna obrada za port, posto je on neophodan parametar...
    def autoResolvedPort = tryToResolvePort(config) 
    if (autoResolvedPort)
    	config.port = autoResolvedPort
    else {
        def newPort = JOptionPane.showInputDialog(null, 
            'Na zalost, nisam bio u stanju da automatski saznam koji port koristi modem.\n'+
            'Procitajte uputstvo kako da dodjete do ove informacije pa je unesite ovde (npr. COM7):', 
            'Nepoznat port modema',
            JOptionPane.INFORMATION_MESSAGE)
        if (newPort)
            config.port = newPort
    }
    return config
  }
  
  def tryToResolvePort(config) {
	  def result = null
	  try {
		  def extractor = new RegistryExtractor()
	        
	      def identifier = extractor.extractValueOfRegistryKey(
	    		  config.descriptor.@location.text(), 
	    		  config.descriptor.@key.text())
	        
	      result = extractor.extractValueOfRegistryKey(
	    		  config.identifier.@location.text().replace('{identifier}',identifier), 
	    		  config.identifier.@key.text())
	      
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
    def frame = swing.frame(title:'MtsCheckAccount v0.2',
            location: [100,100],
            resizable: false,
            windowClosing : { event: System.exit(0) }) {
      panel {
        tableLayout {
    	  tr {
    		td { label 'Status: ' }
            td (colfill:true) { edStatus = textField (editable:false, text: 'Ucitavanje') }
          }
          tr {
            td (colspan:2) { label ' ' }
          }
          tr {
            td { label 'Zaduzenje: ' }
            td (colfill:true) { edZaduzenje = textField (editable:false, text: "MOLIM, SACEKAJTE.......") }
          }
          tr {
            td { label 'Stanje: ' }
            td (colfill:true) { edStanje = textField (editable:false, text: "MOLIM, SACEKAJTE.......") }
          }
          tr {
            td (colspan:2, colfill:true) { label ' ' }
          }
          tr {
            td (colspan:2, colfill:true) { label 'Preostali besplatni saobracaj:' }
          }
          tr {
            td { label 'U mrezi mt:s: ' }
            td (colfill:true) { edUMrezi = textField (editable:false, text: "MOLIM, SACEKAJTE.......") }
          }
          tr {
            td { label 'Van mreze: ' }
            td (colfill:true) { edVanMreze = textField (editable:false, text: "MOLIM, SACEKAJTE.......") }
          }
          tr {
            td { label 'SMS: ' }
            td (colfill:true) { edSms = textField (editable:false, text: "MOLIM, SACEKAJTE.......") }
          }
          tr {
            td () { label 'Gprs: ' }
            td (colfill:true) { edGprs = textField (editable:false, text: "MOLIM, SACEKAJTE.......") }
          }
          tr {
            td (colspan:2, colfill:true) { label ' ' }
          }
          tr {
            td (colspan:2, align:'CENTER') { label('milan.aleksic@gmail.com', 
            		font: new Font(null, Font.BOLD, 10),
            		cursor: Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
            		mouseClicked: {event: Desktop.getDesktop().mail('mailto:milan.aleksic@gmail.com'.toURI())} ) }
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
      provide ( params() ) { info ->
        println info
        if (info instanceof String)
        	edStatus.text = info
    	else if (info instanceof InformationBean) {
    		edZaduzenje.text = info.Zaduzenje
            edStanje.text = info.Stanje
            edUMrezi.text = info.UMrezi
            edVanMreze.text = info.VanMreze
            edSms.text = info.Sms
            def gprstext = null 
            try {
                def inMB = new BigDecimal(Long.parseLong(info.Gprs.trim()) / 1024).setScale(2, BigDecimal.ROUND_CEILING)
                gprstext = "${info.Gprs}KB (${inMB}MB)"
            } catch (Throwable t) {
                gprstext = info.Gprs;
            }
            edGprs.text = gprstext	
    	}
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