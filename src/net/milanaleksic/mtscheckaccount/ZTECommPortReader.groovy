package net.milanaleksic.mtscheckaccount

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
	int tickCount = 0
    while (!(lastRead =~ whatToWaitFor)) {
      Thread.sleep(100)
      tickCount++
      if (tickCount>=100)
    	  throw new IllegalStateException("Nije dobijen odgovor od modema. Moguce je da je doslo do problema u obradi, restartujte program")
    }
	def returnValue=lastRead
	lastRead = null
    return returnValue
  }

}