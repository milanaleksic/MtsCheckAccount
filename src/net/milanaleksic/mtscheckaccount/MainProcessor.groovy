package net.milanaleksic.mtscheckaccount

import org.apache.commons.logging.*
import net.milanaleksic.mtscheckaccount.data.*;
import net.milanaleksic.mtscheckaccount.os.*;

import groovy.swing.SwingBuilder
import javax.swing.JOptionPane
import java.awt.*
import java.awt.event.KeyEvent
import net.milanaleksic.mtscheckaccount.util.ApplicationUtil

public class MainProcessor {

    private static Log log = LogFactory.getLog(MainProcessor.class)

    def InformationProvider DataProvider

    def edStanje, edUMrezi, edVanMreze, edSms, edGprs, edStatus

    def programVersion
    
    public MainProcessor() {
        ApplicationUtil.startInternetConnectibilityThread()
        def config = readConfig()
        DataProvider = ProviderFactory.fromConfig(config)
        DataProvider.locator = LocatorFactory.fromConfig(config)
    }

    def readConfig() {
        programVersion = ApplicationUtil.getApplicationVersion()
        log.info "Mts Check Account program version: $programVersion"
        def config
        try {
            config = new XmlSlurper().parse('config.xml')
        }
        catch (Throwable t) {
            throw new RuntimeException('Greska - nisam uspeo da otvorim konfiguraciju', t)
        }
        return config
    }

    def provide(Closure closure) {
        return DataProvider.provideInformation(closure)
    }

    def showForm() {
        def swing = new SwingBuilder()
        ApplicationUtil.setWindowsTheme()

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
                                        File readMeFile = ApplicationUtil.openProgramRootFile("ProcitajMe.txt")
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
                                        File licenceFile = ApplicationUtil.openProgramRootFile("License.txt")
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
                    tr {
                        td(colspan: 2) { label ' ' }
                    }
                    tr {
                        td(colspan: 2, colfill: true) { edStatus = textField(editable: false, text: 'Ucitavanje', font: new Font(null, Font.BOLD, 12), keyPressed: keyPressedEventHandler) }
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
            provide { info ->
                log.info "Setting status text: [$info]"
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
