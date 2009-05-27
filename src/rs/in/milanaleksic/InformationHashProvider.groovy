package rs.in.milanaleksic
/**
 * Created by IntelliJ IDEA.
 * User: Milan Aleksic
 * Date: 14-May-2009
 * Time: 22:09:16
 * To change this template use File | Settings | File Templates.
 */

public interface InformationProvider {
  def provideInformation(parameterHash, Closure closure)
}