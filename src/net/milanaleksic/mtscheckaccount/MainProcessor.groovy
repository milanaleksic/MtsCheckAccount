package net.milanaleksic.mtscheckaccount

import groovy.swing.SwingBuilder
import javax.swing.JOptionPane

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
    return result
  }

  def provide(params, Closure closure) {
    if (!params)
      return null
    return DataProvider.provideInformation(params, closure)
  }

  def showForm() {
    def swing = new SwingBuilder()
    def frame = swing.frame(title:'ZTE MF622 3G Modem - mt:s CheckPostpaidAccount v0.2',
            location: [100,100],
            resizable: false,
            windowClosing : { event: System.exit(0) }) {
      panel {
        tableLayout {
          tr {
            td { label 'Zaduzenje' }
            td { edZaduzenje = textField (editable:false, text: "molim, sacekajte...") }
          }
          tr {
            td { label 'Stanje' }
            td { edStanje = textField (editable:false, text: "molim, sacekajte...") }
          }
          tr {
            td (colspan:2) { label ' ' }
          }
          tr {
            td (colspan:2) { label 'Preostali besplatni saobracaj:' }
          }
          tr {
            td () { label 'U mrezi mt:s' }
            td { edUMrezi = textField (editable:false, text: "molim, sacekajte...") }
          }
          tr {
            td () { label 'Van mreze:' }
            td { edVanMreze = textField (editable:false, text: "molim, sacekajte...") }
          }
          tr {
            td () { label 'SMS:' }
            td { edSms = textField (editable:false, text: "molim, sacekajte...") }
          }
          tr {
            td () { label 'Gprs(KB):' }
            td { edGprs = textField (editable:false, text: "molim, sacekajte...") }
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
        edGprs.text = informationBean.Gprs
      }
    } catch (RuntimeException t) {
      JOptionPane.showMessageDialog(null, "($t)\n${t.getMessage() != null ? t.getMessage() : ''}", 'Greska', JOptionPane.ERROR_MESSAGE)
      t.printStackTrace()
      System.exit(1);
    } catch (Throwable t) {
      JOptionPane.showMessageDialog(null, "($t)\n${t.getMessage() != null ? t.getMessage() : ''}", 'Greska', JOptionPane.ERROR_MESSAGE)
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