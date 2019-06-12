# Https App
Spring Boot application to poc https. Accessible on port 8443, by default

Create a self-signed SSL cert, for server:
> keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650
> keytool -list -v -storetype pkcs12 -keystore keystore.p12


More keygen options.
keytool -genkeypair
        -alias server-keypair
        -keyalg RSA
        -storetype PKCS12
        -keysize 2048
        -validity 3650
        -dname "CN=server,O=mydomain.com"
        -keypass server-key-p455w0rd
        -keystore server-keystore.p12
        -storepass server-keystore-p455w0rd

-storetype PKCS12 or JKS (lots of others, too)

Build and package:
> mvn clean package


Execute:
> java -Dserver.ssl.key-store-password=password -Dserver.ssl.key-store=/home/foo/keystore.p12 -jar ./target/https-app-0.0.1-SNAPSHOT.jar
or
> java -Dserver.ssl.key-store-password=password -Dserver.ssl.key-store=/home/foo/keystore.jks -jar ./target/http-app-0.0.1-SNAPSHOT.jar


Access greeting, via https (test server):
> https://localhost:8443/greeting?name=Bob

Access page via https, which then calls /greeting, via https/ssl (test server & client):
> https://localhost:8443/hello




Populate keystore with public key, required for accessing the https service.

PKDS12 Keystores:
~~~~~~~~~~~~~~~~~
Export public key from keystore.p12 , for use by client - create server-public-key.cer:
keytool -exportcert -alias tomcat -file server-public-key.cer -keystore keystore.p12 -storepass password


Import server public cert/key (server-public-key.cer) into client trust store - create client-truststore.p12:
keytool -importcert -alias server-public-key -keyalg RSA -storetype PKCS12 -keystore client-truststore.p12  -file server-public-key.cer -storepass password -noprompt


JKS Keystore:
~~~~~~~~~~~~~
Export public key from keystore.jks , for use by client - create server-public-key.cer:
keytool -exportcert -alias tomcat -file server-public-key.crt -keystore keystore.jks -storepass password


Import server public cert/key (server-public-key.cer) into client trust store - create client-truststore.jks:
keytool -importcert -alias server-public-key -keyalg RSA -storetype PKCS12 -keystore client-truststore.p12  -file server-public-key.crt -storepass password -noprompt


After creating another keystore, with the public key, add code to ensure Tomcat.Spring picks it up:
        String certificatesTrustStorePath = "/<mypath>/server-public-key";
        System.setProperty("javax.net.ssl.trustStore", certificatesTrustStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", "password");




Add certificate to JRE Security cacerts
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Alternative advice pointed to just putting the certificate into the default cacerts (see below)
keytool -import -v -trustcacerts -alias localhost -file ~/Applications/https-app/server-public-key.crt -keystore ./cacerts -keypass changeit -storepass changeit

