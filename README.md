# Https App
Spring Boot application to poc https. Accessible on port 8443, by default

Create a self-signed SSL cert:
> keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650
> keytool -list -v -storetype pkcs12 -keystore keystore.p12



Build and package:
> mvn clean package


Execute:
> java -Dserver.ssl.key-store-password=password -Dserver.ssl.key-store=/home/foo/keystore.p12 -jar ./target/https-app-0.0.1-SNAPSHOT.jar


Access:
https://localhost:8443/greeting?name=Bob


