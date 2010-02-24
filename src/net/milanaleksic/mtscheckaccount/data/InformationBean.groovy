package net.milanaleksic.mtscheckaccount.data

public class InformationBean {
    def Stanje= "NEDOSTUPNO"
    def UMrezi= "NEDOSTUPNO"
    def VanMreze= "NEDOSTUPNO"
    def Sms= "NEDOSTUPNO"
    def Gprs= "NEDOSTUPNO"
    
    public String toString() {
    	return "Stanje=[$Stanje],UMrezi=[$UMrezi], VanMreze=[$VanMreze], Sms=[$Sms], Gprs=[$Gprs]"
    }
}