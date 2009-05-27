package rs.in.milanaleksic

/**
 * Created by IntelliJ IDEA.
 * User: Milan Aleksic
 * Date: 16.05.2009.
 * Time: 20.03.25
 */

public class MTSExtract {

  def extract(readFromData) {
    def result = new InformationBean()
    
    [
            Zaduzenje:(/Zaduzenje/),
            Stanje:(/Stanje/),
            UMrezi:(/U mrezi mts/),
            VanMreze:(/Van mreze/),
            Sms:(/Sms/),
            Gprs:(/Gprs[(]KB[)]/)
    ].each { informationBeanPropertyName, nameOfRowInData ->
      def distinctRow = readFromData.find(nameOfRowInData+/: .*/)
      result."$informationBeanPropertyName" = distinctRow ? distinctRow.find(/\s[\d.]+/) : "NEDOSTUPNO"
    }

    return result
  }

}