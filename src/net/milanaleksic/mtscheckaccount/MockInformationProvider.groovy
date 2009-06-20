package net.milanaleksic.mtscheckaccount
/**
 * Created by IntelliJ IDEA.
 * User: Milan Aleksic
 * Date: 14-May-2009
 * Time: 22:26:20
 * To change this template use File | Settings | File Templates.
 */

public class MockInformationProvider implements InformationProvider {

  public def provideInformation(params, Closure closure) {
	closure 'Mozete ugasiti program'
	
	def testReceivedFromMts ="""
+CUSD: 1,"Zaduzenje: 0.00
Stanje: 1.70
Preostali besplatni saobracaj:
U mrezi mts: 1
Van mreze: 2
Sms: 3
Gprs(KB): ${5*1024*1024}",15
"""
	
    closure(new MTSExtract().extract(testReceivedFromMts))
  }
}