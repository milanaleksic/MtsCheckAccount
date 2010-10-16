package net.milanaleksic.mtscheckaccount.data

import javax.swing.JOptionPane

@Singleton public class MTSExtract {

    def extract(readFromData) {
        def result = new InformationBean()

        def negativeOutcomes = [
                /Ova usluga trenutno nije dostupna/,
                /Servis trenutno nije dostupan.../
        ]

        for (outcome in negativeOutcomes) {
            if (readFromData.indexOf(outcome) != -1) {
                JOptionPane.showMessageDialog(null, 'Dobri ljudi iz mt:s-a su rekli da je servis trenutno nedostupan i mole Vas da pokusate kasnije :)')
                return result
            }
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
            throw new RuntimeException("Problem u toku parsiranja odgovora za segmentima")
        }
        return result
    }

}