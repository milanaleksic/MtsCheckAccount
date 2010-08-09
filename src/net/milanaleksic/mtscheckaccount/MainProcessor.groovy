package net.milanaleksic.mtscheckaccount

import net.milanaleksic.mtscheckaccount.data.*;
import net.milanaleksic.mtscheckaccount.os.*;

import groovy.swing.SwingBuilder
import javax.swing.JOptionPane
import java.awt.*
import java.awt.event.KeyEvent
import javax.swing.UIManager

public class MainProcessor {

    InformationProvider DataProvider
    Locator Locator

    def edStanje, edUMrezi, edVanMreze, edSms, edGprs
    def edStatus

    def config
    def programVersion
    
    MainProcessor() {
        readConfig()
        activateInternetConnectibilityThread()
        Locator = LocatorFactory.fromConfig(config)
        DataProvider = ProviderFactory.fromConfig(config)
    }

    def activateInternetConnectibilityThread() {
        def internetAccessRunnable = {
            Socket clientSocket = null
            try {
                clientSocket = new Socket("www.google.com", 80)
                println 'Internet JESTE dostupan'
                JOptionPane.showMessageDialog(null, 'Imate pristup Internetu. Ukoliko je jedini kanal koji Vam dopusta da izadjete na Internet 3G modem, onda ovaj program uopste ne mozete koristiti dok se ne iskljucite sa njega.\nRazlog: modem moze da koristi ili aplikacija za pristup Internetu ili ovaj program, ne mogu oba istovremeno.', 'Upozorenje', JOptionPane.WARNING_MESSAGE)
                return
            } catch (IOException exc) {
                println 'Internet nije dostupan'
            } finally {
                if (clientSocket)
                    clientSocket.close()
            }
        }
        def internetAccessThread = new Thread(internetAccessRunnable)
        internetAccessThread.daemon = true
        internetAccessThread.start()
    }

    def readConfig() {
        File versionFile = new File("version.txt")
        if (versionFile.exists()) {
            programVersion = versionFile.text.trim()
        } else {
            programVersion = '?'
        }
        
        println "Mts Check Account program version: $programVersion"
        config = null
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
        def port
        try {
            port = Locator.getModemLocation(params)
            if (!port)
                throw new IllegalArgumentException("Automatska pretraga za modemom nije urodila plodom")
            return port
        } catch (t) {
            def manualPort = JOptionPane.showInputDialog(null,
                    'Na zalost, nisam bio u stanju da automatski saznam koji port koristi modem.\n' +
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

    def File openProgramRootFile(def filename) {
        String thisDirPath = new File(".").absolutePath
        File thisDir = new File(thisDirPath).parentFile
        File rootDirectory = thisDir.parentFile
        return new File(rootDirectory.absolutePath+File.separator+filename)
    }

    def showForm() {
        def swing = new SwingBuilder()
        try {
            UIManager.setLookAndFeel('com.sun.java.swing.plaf.windows.WindowsLookAndFeel')
        } catch (t) {
            println "Windows look & feel not supported"
        }

        def keyPressedEventHandler = { event->
            if (event.keyCode == KeyEvent.VK_ESCAPE) {
                Runtime.getRuntime().exit(0)
            }
        }
        def frame = swing.frame(title: "MtsCheckAccount v$programVersion",
                location: [100, 100],
                resizable: false,
                keyPressed: keyPressedEventHandler,
                windowClosing: { event: System.exit(0) }) {
            panel {
                tableLayout {
                    tr {
                        td(colspan: 2, align: 'CENTER') {
                            label("MtsCheckAccount v$programVersion",
                                    font: new Font(null, Font.BOLD, 12))
                        }
                    }
                    tr {
                        td(colspan: 2, align: 'CENTER') {
                            panel() {
                                button("Procitaj me",
                                    font: new Font(null, Font.BOLD, 10),
                                    actionPerformed: {event->
                                        File readMeFile = openProgramRootFile("ProcitajMe.txt")
                                        if (readMeFile.exists()) {
                                            Desktop.getDesktop().open (readMeFile)
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Tekst ProcitajMe nije dostupan. Verovatno je neophodno da reinstalirate program", 'Greska', JOptionPane.ERROR_MESSAGE)
                                        }
                                    },
                                    keyPressed: keyPressedEventHandler)
                                button("Licenca",
                                    font: new Font(null, Font.BOLD, 10),
                                    actionPerformed: {event->
                                        File licenceFile = openProgramRootFile("License.txt")
                                        if (licenceFile.exists()) {
                                            Desktop.getDesktop().open (new File(licenceFile.absolutePath))
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Tekst licence nije dostupan. Verovatno je neophodno da reinstalirate program", 'Greska', JOptionPane.ERROR_MESSAGE)
                                        }
                                    },
                                    keyPressed: keyPressedEventHandler)
                                button("Izlaz",
                                    font: new Font(null, Font.BOLD, 10),
                                    actionPerformed: {event->
                                        Runtime.getRuntime().exit(0)
                                    },
                                    keyPressed: keyPressedEventHandler)
                            }
                        }
                    }
                    tr {
                        td(colspan: 2, align: 'CENTER') {
                            label('milan.aleksic@gmail.com',
                                    font: new Font(null, Font.BOLD, 10),
                                    foreground: Color.BLUE,
                                    cursor: Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
                                    mouseClicked: {event: Desktop.getDesktop().mail('mailto:milan.aleksic@gmail.com'.toURI())})
                        }
                    }
                    tr {
                        td(colspan: 2, align: 'CENTER') {
                            label('Posetite web stranicu programa',
                                    font: new Font(null, Font.BOLD, 10),
                                    foreground: Color.BLUE,
                                    cursor: Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
                                    mouseClicked: {event: Desktop.getDesktop().browse('http://www.milanaleksic.net/#/projects/mtscheckaccount'.toURI())})
                        }
                    }
                    tr {
                        td(colspan: 2, colfill: true) { label ' ' }
                    }
                    tr {
                        td { label 'Status: ' }
                        td(colfill: true) { edStatus = textField(editable: false, text: 'Ucitavanje', font: new Font(null, Font.BOLD, 12), keyPressed: keyPressedEventHandler) }
                    }
                    tr {
                        td(colspan: 2) { label ' ' }
                    }
                    tr {
                        td { label 'Stanje: ' }
                        td(colfill: true) { edStanje = textField(editable: false, text: "MOLIM, SACEKAJTE.......", keyPressed: keyPressedEventHandler) }
                    }
                    tr {
                        td(colspan: 2, colfill: true) { label ' ' }
                    }
                    tr {
                        td(colspan: 2, colfill: true) { label 'Preostali besplatni saobracaj:' }
                    }
                    tr {
                        td { label 'U mrezi mt:s: ' }
                        td(colfill: true) { edUMrezi = textField(editable: false, text: "MOLIM, SACEKAJTE.......", keyPressed: keyPressedEventHandler) }
                    }
                    tr {
                        td { label 'Van mreze: ' }
                        td(colfill: true) { edVanMreze = textField(editable: false, text: "MOLIM, SACEKAJTE.......", keyPressed: keyPressedEventHandler) }
                    }
                    tr {
                        td { label 'SMS: ' }
                        td(colfill: true) { edSms = textField(editable: false, text: "MOLIM, SACEKAJTE.......", keyPressed: keyPressedEventHandler) }
                    }
                    tr {
                        td() { label 'Gprs: ' }
                        td(colfill: true) { edGprs = textField(editable: false, text: "MOLIM, SACEKAJTE.......", keyPressed: keyPressedEventHandler) }
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
            provide(config) { info ->
                println info
                if (info instanceof String)
                    edStatus.text = info
                else if (info instanceof InformationBean) {
                    edStanje.text = info.Stanje
                    edUMrezi.text = info.UMrezi
                    edVanMreze.text = info.VanMreze
                    edSms.text = info.Sms
                    def gprstext
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
            t.printStackTrace()
            edStatus.text = 'GRESKA U OBRADI!'
            edUMrezi.text = edStanje.text = edVanMreze.text = edSms.text = edGprs.text = '?'
            JOptionPane.showMessageDialog(null, "(${t.class})\n${t.getMessage() != null ? t.getMessage() : ''}", 'Greska', JOptionPane.ERROR_MESSAGE)
        }
    }

}
