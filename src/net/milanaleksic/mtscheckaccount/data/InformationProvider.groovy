package net.milanaleksic.mtscheckaccount.data

import net.milanaleksic.mtscheckaccount.os.Locator

public abstract class InformationProvider {

    def Locator locator

    def abstract provideInformation(params, Closure closure)

}