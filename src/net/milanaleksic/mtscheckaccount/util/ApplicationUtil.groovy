package net.milanaleksic.mtscheckaccount.util

import javax.swing.UIManager
import org.apache.commons.logging.*
import javax.swing.JOptionPane

class ApplicationUtil {

    private static Log log = LogFactory.getLog(ApplicationUtil.class)

    public static String getApplicationVersion() {
        File versionFile = new File("version.txt")
        if (versionFile.exists()) {
            return versionFile.text.trim()
        } else {
            return '?'
        }
    }

    public static File openProgramRootFile(def filename) {
        String thisDirPath = new File(".").absolutePath
        File thisDir = new File(thisDirPath).parentFile
        File rootDirectory = thisDir.parentFile
        return new File(rootDirectory.absolutePath+File.separator+filename)
    }

    public static void setWindowsTheme() {
        try {
            UIManager.setLookAndFeel('com.sun.java.swing.plaf.windows.WindowsLookAndFeel')
        } catch (t) {
            log.warn "Windows look & feel not supported"
        }
    }

    public static startInternetConnectibilityThread() {
        def internetAccessRunnable = {
            Socket clientSocket = null
            try {
                clientSocket = new Socket("www.google.com", 80)
                log.warn 'Internet JESTE dostupan'
                JOptionPane.showMessageDialog(null, 'Imate pristup Internetu. Ukoliko je jedini kanal koji Vam dopusta da izadjete na Internet 3G modem, onda ovaj program uopste ne mozete koristiti dok se ne iskljucite sa njega.\nRazlog: modem moze da koristi ili aplikacija za pristup Internetu ili ovaj program, ne mogu oba istovremeno.', 'Upozorenje', JOptionPane.WARNING_MESSAGE)
                return
            } catch (IOException exc) {
                log.debug 'Internet nije dostupan'
            } finally {
                if (clientSocket)
                    clientSocket.close()
            }
        }
        def internetAccessThread = new Thread(internetAccessRunnable)
        internetAccessThread.daemon = true
        internetAccessThread.start()
    }
}
