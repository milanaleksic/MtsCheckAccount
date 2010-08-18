package net.milanaleksic.mtscheckaccount.data.adaptive

import net.milanaleksic.mtscheckaccount.data.*
import gnu.io.*
import groovy.util.slurpersupport.GPathResult
import net.milanaleksic.mtscheckaccount.util.PDUConverter

public class AdaptiveInformationProvider extends InformationProvider {

    def GPathResult params

    public AdaptiveInformationProvider(GPathResult config) {
        super(config)
    }

    public def provideInformation(Closure closure) {
        log.debug "Adaptive Information Provider dohvata informacije za modem ${locator.recognizedModemId} sa porta ${locator.recognizedModemPort}"
        fetchInfoFromLocator()
        executeProcessing(closure)
    }

    private def executeProcessing(Closure closure) {
        def commPort
        def reader = null
        try {
            commPort = openModemPort(params, locator.recognizedModemPort)
            def input = commPort.getInputStream()
            def output = commPort.getOutputStream()
            def str = new PrintStream(output)

            (new Thread(reader = new PortReader(input: input))).start()

            if (params.check.size() != 0) {
                closure 'Proveravam status modema...'
                reader.barrier = preProcess(params.check.@response)
                printToStream(str, preProcess(params.check.@request))

                if ("[[${reader.haltUntilBarrierCrossed()}]]" =~ /: 6/) {
                    closure 'Palim modem...'
                    reader.barrier = preProcess(params.start.@response)
                    printToStream(str, preProcess(params.start.@request))
                    reader.haltUntilBarrierCrossed()
                    Thread.sleep(5000)
                }
            }

            closure 'Pricam...'
            params.pre.each {
                reader.barrier = preProcess(it.@response)
                printToStream(str, preProcess(it.@request))
                reader.haltUntilBarrierCrossed()
            }

            closure 'Saljem glavni zahtev...'
            reader.barrier = preProcess(params.main.@response)
            printToStream(str, preProcess(params.main.@request))
            def response = reader.haltUntilBarrierCrossed()
            closure new MTSExtract().extract(response)

            if (params.post.size() != 0) {
                closure 'Gasim modem...'
                params.post.each {
                    reader.barrier = preProcess(it.@response)
                    printToStream(str, preProcess(it.@request))
                    reader.haltUntilBarrierCrossed()
                }
            }

            closure 'Mozete zatvoriti program'

        } finally {
            if (reader)
                reader.Shutdown = true
            try {
                if (commPort != null)
                    commPort.close()
            } catch (Throwable t) {}
        }
    }

    private def openModemPort(params, String port) {
        def portIdentifier, commPort
        try {
            log.debug "Adaptive Information Provider pristupa portu ${port}"
            portIdentifier = CommPortIdentifier.getPortIdentifier(port)
        } catch (Throwable t) {
            t.printStackTrace()
            throw new RuntimeException("Proverite uz pomoc uputstva na mom sajtu (www.milanaleksic.net) da li je port ${port} zaista onaj koji se koristi od strane modema.\nUkoliko ne uspete da resite problem, molim procitajte u istom uputstvu kako da mi posaljete log aplikacije.");
        }

        if (portIdentifier.isCurrentlyOwned())
            throw new RuntimeException('Port se trenutno koristi');

        commPort = portIdentifier.open(this.getClass().getName(), 2000)
        if (!(commPort instanceof SerialPort)) {
            throw new RuntimeException('Samo je serijski port dozvoljen');
        }
        commPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)

        return commPort
    }

    private def printToStream(PrintStream stream, String str) {
        log.debug "Saljem komandu modemu: [$str]"
        stream.print str
        stream.print '\r\n'
        stream.flush()
    }

    private def fetchInfoFromLocator() {
        config.data.device.each { device ->
            if (device.@id.toString() == locator.recognizedModemId) {
                params = device
            }
        }
        return params
    }

    private def String preProcess(str) {
        //TODO: zameni {{str}} sa PDU(str)
        return str.text().replaceAll("\\{\\{(.*)\\}\\}", "???\$1???")
    }

}