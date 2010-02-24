package net.milanaleksic.mtscheckaccount.os.win

import java.lang.reflect.Method
import java.util.prefs.Preferences

public class RegistryTool {
    
    private final int KEY_READ = 0x20019
    
    private final Preferences systemRoot = Preferences.systemRoot()
    private final Class clz = systemRoot.getClass()
    
    private final Method mOpenKey
    private final Method mCloseKey
    private final Method mWinRegQueryValue
    
    public RegistryTool() {
        mOpenKey = clz.getDeclaredMethod('openKey', 
                (Class[]) [byte[].class, int.class, int.class])
        mOpenKey.accessible = true

        mCloseKey = clz.getDeclaredMethod('closeKey', 
                (Class[]) [int.class])
        mCloseKey.accessible = true
        
        mWinRegQueryValue = clz.getDeclaredMethod('WindowsRegQueryValueEx', 
                (Class[]) [int.class, byte[].class])
        mWinRegQueryValue.accessible = true
    }
    
    public String extractValueOfRegistryKey(String subkey, String key) {
        def hSettings = (Integer) mOpenKey.invoke(systemRoot, 
                (Object[]) [toByteArray(subkey), new Integer(KEY_READ), new Integer(KEY_READ)])

        def b = (byte[]) mWinRegQueryValue.invoke(systemRoot, 
                (Object[]) [hSettings, toByteArray(key)])
                
        def identifier = (b != null ? new String(b).trim() : null)
        
        mCloseKey.invoke(Preferences.systemRoot(), 
                (Object[]) [hSettings])
        
        return identifier
    }

    private byte[] toByteArray(String str) {
        byte[] result = new byte[str.length() + 1];
        for (int i = 0; i < str.length(); i++) {
            result[i] = (byte) str.charAt(i);
        }
        result[str.length()] = 0;
        return result;
    }
    
}
