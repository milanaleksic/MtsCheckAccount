package net.milanaleksic.mtscheckaccount.provider.mock

import net.milanaleksic.mtscheckaccount.provider.*

public class MockInformationProvider implements InformationProvider {

  public def provideInformation(params, String port, Closure closure) {
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