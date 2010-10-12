@echo off

call mvn install:install-file -DgroupId=com.ice.jni -DartifactId=registry -Dversion=3.1.3 -Dpackaging=jar -Dfile=registry.jar

call mvn install:install-file -DgroupId=com.ice.jni -DartifactId=registry-win -Dversion=3.1.3 -Dpackaging=zip -Dfile=jniregistry.zip

call mvn install:install-file -DgroupId=gnu.io -DartifactId=rxtx -Dversion=2.2.0.0-SNAPSHOT -Dpackaging=jar -Dfile=RXTXcomm.jar

call mvn install:install-file -DgroupId=gnu.io -DartifactId=rxtx-serial-win-x86 -Dversion=2.2.0.0-SNAPSHOT -Dpackaging=zip -Dfile=rxtx-serial-win-x86.zip

call mvn install:install-file -DgroupId=gnu.io -DartifactId=rxtx-serial-win-ia64 -Dversion=2.2.0.0-SNAPSHOT -Dpackaging=zip -Dfile=rxtx-serial-win-ia64.zip

call mvn install:install-file -DgroupId=gnu.io -DartifactId=rxtx-serial-linux-386 -Dversion=2.2.0.0-SNAPSHOT -Dpackaging=zip -Dfile=rxtx-serial-linux-i386.zip

call mvn install:install-file -DgroupId=gnu.io -DartifactId=rxtx-serial-linux-x86_64 -Dversion=2.2.0.0-SNAPSHOT -Dpackaging=zip -Dfile=rxtx-serial-linux-x86_64.zip
