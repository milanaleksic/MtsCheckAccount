package net.milanaleksic.mtscheckaccount.provider

public interface InformationProvider {
  def provideInformation(params, String port, Closure closure)
}