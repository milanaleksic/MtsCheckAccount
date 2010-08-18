package net.milanaleksic.mtscheckaccount.util

import org.apache.commons.logging.*
import javax.swing.JOptionPane

public class ThreadExcHandler implements Thread.UncaughtExceptionHandler {

    private static Log log = LogFactory.getLog(ThreadExcHandler.class)

    void uncaughtException(Thread thr, Throwable t) {
        log.error('Greska u obradi u niti', t)
        JOptionPane.showMessageDialog(null, "[$thr]\n($t)\n${t.getMessage() != null ? t.getMessage() : ''}", 'Greska', JOptionPane.ERROR_MESSAGE)
        System.exit(1);
    }
    
}
