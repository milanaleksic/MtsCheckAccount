package net.milanaleksic.mtscheckaccount

import javax.swing.JOptionPane

import gnu.io.*

public class ZTEMF622InformationProvider implements InformationProvider {

  public def provideInformation(parameterHash, Closure closure) {

    def commPort = null
    def portIdentifier
    def reader = null
    
    try {

      try {
        portIdentifier = CommPortIdentifier.getPortIdentifier(parameterHash.port)
      } catch (Throwable t) {
        throw new RuntimeException("Proverite uz pomoc uputstva da li je port ${parameterHash.port} zaista onaj koji se koristi od strane modema");
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

      println 'Proveravam status modema...'
      str.println 'AT+ZOPRT?'
      def modemStatus= "[[${reader.waitFor(/([+]+)ZOPRT/)}]]"

      if (modemStatus =~ /: 6/) {
        println 'Palim modem...'
        str.println 'AT+ZOPRT=5'
        reader.waitFor(/\s*([+]+)ZPASR/)
      }

      println 'Saljem zahtev...'
      str.println 'AT+CUSD=1,"*797#",15'
      def response = reader.waitFor(/\s*([+]+)CUSD: 0,"/)
      closure ( new MTSExtract().extract(response) )
      
      println 'Gasim modem...'
      str.println 'AT+ZOPRT=6'
      reader.waitFor(/\s*OK\s*/)
      
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