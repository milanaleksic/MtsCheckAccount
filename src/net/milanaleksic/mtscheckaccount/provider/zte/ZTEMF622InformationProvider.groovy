package net.milanaleksic.mtscheckaccount.provider.zte

import net.milanaleksic.mtscheckaccount.provider.*
import javax.swing.JOptionPane
import gnu.io.*

public class ZTEMF622InformationProvider implements InformationProvider {

  public def provideInformation(params, Closure closure) {

    def commPort = null
    def portIdentifier
    def reader = null
    
    try {

      try {
    	  println "ZTEMF622 Information Provider pristupa portu ${params.port.text()}"
   		  portIdentifier = CommPortIdentifier.getPortIdentifier(params.port.text())
      } catch (Throwable t) {
    	  t.printStackTrace()
          throw new RuntimeException("Proverite uz pomoc uputstva da li je port ${params.port} zaista onaj koji se koristi od strane modema");
      }

      if (portIdentifier.isCurrentlyOwned())
        throw new RuntimeException('Port se trenutno koristi');

      commPort = portIdentifier.open(this.getClass().getName(), 2000)
      if (!(commPort instanceof SerialPort)) {
        throw new RuntimeException('Samo je serijski port dozvoljen');
      }
      commPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)

      def input = commPort.getInputStream()
      def output = commPort.getOutputStream()
      (new Thread(reader = new ZTECommPortReader(input:input))).start()

      def str = new PrintStream(output)

      closure 'Proveravam status modema...'
      reader.barrier = params.check.@response
      str.println params.check.@request
      
      if ("[[${reader.haltUntilBarrierCrossed()}]]" =~ /: 6/) {
    	  closure 'Palim modem...'
    	  reader.barrier = params.start.@response
    	  str.println params.start.@request
    	  reader.haltUntilBarrierCrossed()
    	  Thread.sleep(5000)
      }
      
      closure 'Pricam...'
      params.prepare.each {
    	  reader.barrier = it.@response
          str.println it.@request
          reader.haltUntilBarrierCrossed()
      }

      closure 'Saljem glavni zahtev...'
      reader.barrier = params.main.@response
      str.println params.main.@request
      def response = reader.haltUntilBarrierCrossed() 
      closure  new MTSExtract().extract(response)
      
      closure 'Gasim modem...'
      params.stop.each {
    	  reader.barrier = it.@response
          str.println it.@request
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

}