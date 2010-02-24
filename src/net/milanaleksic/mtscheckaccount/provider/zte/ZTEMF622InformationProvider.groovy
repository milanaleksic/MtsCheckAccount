package net.milanaleksic.mtscheckaccount.provider.zte

import net.milanaleksic.mtscheckaccount.provider.*
import javax.swing.JOptionPane
import gnu.io.*

public class ZTEMF622InformationProvider implements InformationProvider {
	
  public def provideInformation(params, String port, Closure closure) {
    def commPort = null
    def reader = null
    try {
      commPort = openModemPort(params, port)
      def input = commPort.getInputStream()
      def output = commPort.getOutputStream()
      def str = new PrintStream(output)

      (new Thread(reader = new ZTECommPortReader(input:input))).start()

      closure 'Proveravam status modema...'
      reader.barrier = params.provider.check.@response
	  printToStream(str, params.provider.check.@request.text())

      if ("[[${reader.haltUntilBarrierCrossed()}]]" =~ /: 6/) {
    	closure 'Palim modem...'
    	reader.barrier = params.provider.start.@response
    	printToStream(str, params.provider.start.@request.text())
    	reader.haltUntilBarrierCrossed()
    	Thread.sleep(5000)
      }
      
      closure 'Pricam...'
      params.provider.pre.each {
    	reader.barrier = it.@response
    	printToStream(str, it.@request.text())
        reader.haltUntilBarrierCrossed()
      }

      closure 'Saljem glavni zahtev...'
      reader.barrier = params.provider.main.@response
	  printToStream(str, params.provider.main.@request.text())
      def response = reader.haltUntilBarrierCrossed() 
      closure new MTSExtract().extract(response)
      
      closure 'Gasim modem...'
      params.provider.post.each {
    	reader.barrier = it.@response
    	printToStream(str, it.@request.text())
        reader.haltUntilBarrierCrossed()
      }
      
      closure 'Mozete zatvoriti program'
      
    } finally {
      if (reader)
        reader.Shutdown = true
      try {
	    if (commPort)
	      commPort.close()
      } catch (Throwable t) {}
    }
  }
  
  private def openModemPort(params, String port) {
	def portIdentifier, commPort
	try {
	  println "ZTEMF622 Information Provider pristupa portu ${port}"
	  portIdentifier = CommPortIdentifier.getPortIdentifier(port)
	} catch (Throwable t) {
	  t.printStackTrace()
	  throw new RuntimeException("Proverite uz pomoc uputstva da li je port ${port} zaista onaj koji se koristi od strane modema");
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
	println "Saljem komandu modemu: [$str]"
	stream.print str
	stream.print '\r\n'
	stream.flush()
  }

}