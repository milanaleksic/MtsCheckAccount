package net.milanaleksic.mtscheckaccount.util

public class ThreadExcHandler implements Thread.UncaughtExceptionHandler {
    void uncaughtException(Thread thr, Throwable t) {
        JOptionPane.showMessageDialog(null, "[$thr]\n($t)\n${t.getMessage() != null ? t.getMessage() : ''}", 'Greska', JOptionPane.ERROR_MESSAGE)
        e.printStackTrace();
        System.exit(1);
    }
}
