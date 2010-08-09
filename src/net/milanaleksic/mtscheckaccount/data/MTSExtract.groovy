package net.milanaleksic.mtscheckaccount.data

import javax.swing.JOptionPane

public class MTSExtract {

    def extract(readFromData) {
        def result = new InformationBean()

        if (readFromData.indexOf(/Ova usluga trenutno nije dostupna/) != -1) {
            JOptionPane.showMessageDialog(null, 'Dobri ljudi iz mt:s-a su rekli da je servis trenutno nedostupan i mole Vas da pokusate kasnije :)')
            return result
        }

        try {
            [Stanje: (/Stanje/),
                    UMrezi: (/Minuti u mts/),
                    VanMreze: (/Minuti van mts/),
                    Sms: (/SMS/),
                    Gprs: (/Podaci [(]kb[)]/)
            ].each { informationBeanPropertyName, nameOfRowInData ->
                (readFromData =~ /$nameOfRowInData\s*:\s*[\d.]+/).each { match ->
                    (match =~ /[\s:][\d.]+/).each { match2 ->
                        result."$informationBeanPropertyName" = match2.replaceAll(':','')
                    }
                }
            }
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(null, "(${t.class})\n${t.getMessage() != null ? t.getMessage() : ''}", 'Greska', JOptionPane.ERROR_MESSAGE)
            t.printStackTrace()
        }
        return result
    }

}