package net.milanaleksic.mtscheckaccount

import net.milanaleksic.mtscheckaccount.data.*;
import net.milanaleksic.mtscheckaccount.os.*;

import groovy.swing.SwingBuilder
import javax.swing.JOptionPane
import java.awt.*

public class MainProcessor {

  InformationProvider DataProvider
  Locator Locator

  def edStanje, edUMrezi, edVanMreze, edSms, edGprs
  def edStatus
  
  def config
  
  MainProcessor() {
	  readConfig()
	  prepareDependencies()
  }
  
  def prepareDependencies() {
	  switch (config.core.os.toString()) {
		  case "real": 
			  Locator = LocatorFactory.createOSLocator()
			  break
		  case "mock":
			  Locator = LocatorFactory.createMockLocator()
			  break
		  default:
			  throw new IllegalArgumentException("Nepoznat os - ${config.core.os.toString()}")
	  }
	  switch (config.core.data.toString()) {
		  case "real": 
			  DataProvider = ProviderFactory.createZTEMF622InformationProvider()
			  break
		  case "mock":
			  DataProvider = ProviderFactory.createMockInformationProvider()
			  break
		  default:
			  throw new IllegalArgumentException("Nepoznat data - ${config.core.data.toString()}")
	  }
  }

  def readConfig() {
    config=null
    try {
      config = new XmlSlurper().parse('config.xml')
    }
    catch (Throwable t) {
      t.printStackTrace()
      println 'Greska - nisam uspeo da otvorim konfiguraciju'
      System.exit(1)
    }
    return config
  }
  
  def extractPort(params) {
	def port = null 
	try {
		port = Locator.getModemLocation(params)
		if (!port)
			throw new IllegalArgumentException("Automatska pretraga za modemom nije urodila plodom")
		return port
	} catch (t) {
		def manualPort = JOptionPane.showInputDialog(null, 
            'Na zalost, nisam bio u stanju da automatski saznam koji port koristi modem.\n'+
            'Procitajte uputstvo kako da dodjete do ove informacije pa je unesite ovde (npr. COM7):', 
            'Nepoznat port modema',
            JOptionPane.INFORMATION_MESSAGE)
        if (!manualPort)
        	manualPort = Locator.getDefaultModemLocation(params)
        if (!manualPort) {
        	println "Neuspesno rucno postavljen port, a takodje ni podrazumevana konfiguracija nije bila dobra"
            System.exit(2)
        }
		return manualPort
	}
  }
  
  def provide(params, Closure closure) {
    if (!params)
      return null
    return DataProvider.provideInformation(params, extractPort(params), closure)
  }

  def showForm() {
    def swing = new SwingBuilder()
    try {
    	swing.lookAndFeel('com.sun.java.swing.plaf.windows.WindowsLookAndFeel')	
    } catch (t) {
    	println "Windows look & feel not supported"
    }
    
    def frame = swing.frame(title:'MtsCheckAccount v0.2.2',
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
      provide ( config ) { info ->
        println info
        if (info instanceof String)
        	edStatus.text = info
    	else if (info instanceof InformationBean) {
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
    } catch (Throwable t) {
      JOptionPane.showMessageDialog(null, "(${t.class})\n${t.getMessage() != null ? t.getMessage() : ''}", 'Greska', JOptionPane.ERROR_MESSAGE)
      t.printStackTrace()
      System.exit(3);
    }
  }

}
