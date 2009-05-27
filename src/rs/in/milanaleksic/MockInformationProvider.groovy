package rs.in.milanaleksic
/**
 * Created by IntelliJ IDEA.
 * User: Milan Aleksic
 * Date: 14-May-2009
 * Time: 22:26:20
 * To change this template use File | Settings | File Templates.
 */

public class MockInformationProvider implements InformationProvider {

  public def provideInformation(parameterHash, Closure closure) {
    closure(new InformationBean(
            Stanje:"100.14",
            Zaduzenje:"1256.12",
            Sms:"14",
            Gprs:"${5*1024*1024}",
            UMrezi: "150",
            VanMreze: "245.19"))
  }
}