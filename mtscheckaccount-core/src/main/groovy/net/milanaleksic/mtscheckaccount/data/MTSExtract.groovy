package net.milanaleksic.mtscheckaccount.data

import javax.swing.JOptionPane

public class MTSExtract {

    def config

    public MTSExtract(config) {
        this.config = config
    }

    def extract(String readFromData) {
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
            [Stanje: getRegexFor('Stanje'),
                    UMrezi: getRegexFor('UMrezi'),
                    VanMreze: getRegexFor('VanMreze'),
                    Sms: getRegexFor('Sms'),
                    Gprs: getRegexFor('Gprs')
            ].each { informationBeanPropertyName, nameOfRowInData ->
                (readFromData =~ /$nameOfRowInData\s*:\s*[\d.]+/).each { match ->
                    (match =~ /[\s:][\d.]+/).each { match2 ->
                        result."$informationBeanPropertyName" = match2.replaceAll(':','').trim()
                    }
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("Problem u toku parsiranja odgovora za segmentima", t)
        }
        return result
    }

    def getRegexFor(String id) {
        def parserInfo = config.parser.info.find { it['@id'].text() == id }
        assert parserInfo
        return ~/${parserInfo['@regex']}/
    }

}