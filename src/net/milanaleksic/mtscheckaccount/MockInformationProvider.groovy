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
+CUSD: 0,"Tarifni profil: Profesionalna tarifa II
Stanje: 125.56
Preostali besplatni saobracaj:
Minuti u mts: 156
Minuti van mts: 215
SMS: 99987
Podaci(kB): 3336380",15
"""
	
    closure(new MTSExtract().extract(testReceivedFromMts))
  }
}