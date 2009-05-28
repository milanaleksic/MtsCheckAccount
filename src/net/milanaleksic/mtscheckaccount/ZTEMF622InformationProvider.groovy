package net.milanaleksic.mtscheckaccount

import javax.swing.JOptionPane

import gnu.io.*

public class ZTEMF622InformationProvider implements InformationProvider {

  public def provideInformation(params, Closure closure) {

    def commPort = null
    def portIdentifier
    def reader = null
    
    try {

      try {
        portIdentifier = CommPortIdentifier.getPortIdentifier(params.port.text())
      } catch (Throwable t) {
        throw new RuntimeException("Proverite uz pomoc uputstva da li je port ${params.port} zaista onaj koji se koristi od strane modema");
      }

      if (portIdentifier.isCurrentlyOwned())
        throw new RuntimeException('Port se trenutno koristi');

      commPort = portIdentifier.open(this.getClass().getName(), 2000)
      if (!(commPort instanceof SerialPort)) {
        throw new RuntimeException('Samo je serijski port dozvoljen');
      }
      commPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)

      def input = commPort.getInputStream()
      def output = commPort.getOutputStream()
      (new Thread(reader = new ZTECommPortReader(input:input))).start()

      def str = new PrintStream(output)

      closure 'Proveravam status modema...'
      str.println params.check.@request
      def checkCommand = params.check.@response
      def modemStatus= "[[${reader.waitFor(/$checkCommand/)}]]"

      if (modemStatus =~ /: 6/) {
    	closure 'Palim modem...'
        str.println params.start.@request
        reader.waitFor(params.start.@response)
      }

      closure 'Saljem zahtev...'
      str.println params.main.@request
      def response = reader.waitFor(params.main.@response) 
      closure  new MTSExtract().extract(response) 
      
      closure 'Gasim modem...'
      str.println params.stop.@request
      reader.waitFor(params.stop.@response)
          
      
      closure 'Mozete ugasiti program'
      
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