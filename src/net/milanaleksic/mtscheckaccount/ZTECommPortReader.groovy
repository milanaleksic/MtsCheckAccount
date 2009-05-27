package net.milanaleksic.mtscheckaccount
/**
 * Created by IntelliJ IDEA.
 * User: Milan Aleksic
 * Date: 14-May-2009
 * Time: 23:29:25
 * To change this template use File | Settings | File Templates.
 */

public class ZTECommPortReader implements Runnable {

  private def InputStream input

  volatile def Shutdown = false
  volatile lastRead = null

  

  public void run() {
	Thread.currentThread().setUncaughtExceptionHandler(new ThreadExcHandler()) 
    byte[] buffer = new byte[1024];
    int len;
    try {
      while ((len = this.input.read(buffer)) > -1) {
        if (!len)
          continue

        lastRead = new String(buffer, 0, len)

        println "[$lastRead]"

        if (Shutdown)
          return
      }
    }
    catch (IOException e) {
      e.printStackTrace()
    }
  }

  public def waitFor(whatToWaitFor) {
    while (!(lastRead =~ whatToWaitFor)) {
      Thread.sleep(200)
    }
    return lastRead
  }

}