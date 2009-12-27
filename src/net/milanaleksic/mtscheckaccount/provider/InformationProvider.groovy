package net.milanaleksic.mtscheckaccount.provider

public interface InformationProvider {
  def provideInformation(params, Closure closure)
}