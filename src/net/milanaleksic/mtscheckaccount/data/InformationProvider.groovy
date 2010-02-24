package net.milanaleksic.mtscheckaccount.data

public interface InformationProvider {
  def provideInformation(params, String port, Closure closure)
}