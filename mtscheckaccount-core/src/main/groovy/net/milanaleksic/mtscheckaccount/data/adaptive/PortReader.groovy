package net.milanaleksic.mtscheckaccount.data.adaptive

import net.milanaleksic.mtscheckaccount.util.ThreadExcHandler
import org.apache.commons.logging.*
import net.milanaleksic.mtscheckaccount.util.PDUConverter

public class PortReader implements Runnable {

    private static Log log = LogFactory.getLog(PortReader.class)

    private def InputStream input

    volatile def Shutdown = false
    volatile lastRead = null
    volatile previouslyConverted = null
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

                if (checkBarrier())
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

    private boolean checkBarrier() {
        if (!whatToWaitFor)
            return false
        if (lastRead =~ whatToWaitFor)
            return true
        if (convertQuotedPartToPDU(lastRead) =~ whatToWaitFor) {
            lastRead = convertQuotedPartToPDU(lastRead)
            return true
        }
        return false
    }

    private String convertQuotedPartToPDU(str) {
        if (!str)
            return str
        String converted = str
        try {
            converted = str.replaceAll("\\\"(.*)\\\"") { all, item ->
                return "\"${PDUConverter.convertPDUToAscii(item)}\""
            }
            if ((converted != str) && (previouslyConverted != converted)) {
                log.debug "ConvertQuotedPartToPDU je konvertovao \"$str\" u \"$converted\""
                previouslyConverted = converted
            }
        } catch (Exception e) {
            log.debug "Nekonvertibilan tekst: [$str]"
        }
        return converted
    }

    public def setBarrier(whatToWaitFor) {
        barrierAlreadyCrossed = false
        this.whatToWaitFor = whatToWaitFor
    }

    public def haltUntilBarrierCrossed() {
        int tickCount = 0
        while (!barrierAlreadyCrossed && !checkBarrier()) {
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