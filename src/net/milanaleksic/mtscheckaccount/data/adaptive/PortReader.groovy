package net.milanaleksic.mtscheckaccount.data.adaptive

import net.milanaleksic.mtscheckaccount.util.ThreadExcHandler
import org.apache.commons.logging.*
import net.milanaleksic.mtscheckaccount.util.PDUConverter

public class PortReader implements Runnable {

    private static Log log = LogFactory.getLog(PortReader.class)

    private def InputStream input

    volatile def Shutdown = false
    volatile lastRead = null
    volatile barrierAlreadyCrossed = false
    volatile whatToWaitFor = null

    public void run() {
        Thread.currentThread().setUncaughtExceptionHandler(new ThreadExcHandler())
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = this.input.read(buffer)) > -1) {
                if (!len)
                    continue

                lastRead = new String(buffer, 0, len)

                if (whatToWaitFor && lastRead =~ whatToWaitFor)
                    barrierAlreadyCrossed = true

                log.debug "[$lastRead]"

                if (Shutdown)
                    return
            }
        }
        catch (IOException e) {
            log.error("IO Greska u toku razgovora sa modemom", e)
            System.exit(2)
        }
    }

    public def setBarrier(whatToWaitFor) {
        barrierAlreadyCrossed = false
        this.whatToWaitFor = whatToWaitFor
    }

    public def haltUntilBarrierCrossed() {
        int tickCount = 0
        while (!barrierAlreadyCrossed && !(lastRead =~ whatToWaitFor)) {
            Thread.sleep(100)
            tickCount++
            if (tickCount >= 100)
                throw new IllegalStateException("Nije dobijen odgovor od modema. Moguce je da je doslo do problema u obradi, restartujte program.\nUkoliko se ova greska ponovi, molim procitajte uputstvo na sajtu (www.milanaleksic.net) kako da ukljucite log i kako da mi isti posaljete.")
        }
        def returnValue = lastRead
        barrierAlreadyCrossed = false
        lastRead = null
        whatToWaitFor = null
        return returnValue
    }

}