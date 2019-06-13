# Https App
Spring Boot application to poc https client/server. Accessible on port 8443, by default




## Create a self-signed SSL cert, for server:

> keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650
> keytool -list -v -storetype pkcs12 -keystore keystore.p12


### More keygen options.
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

-storetype PKCS12 (Standard) or JKS(defaul for Java) (lots of others, too)

## Build and package & test:
> mvn clean package


# Test Server, picking up relevant keystore:
> java -Dserver.ssl.key-store-password=password -Dserver.ssl.key-store=/home/foo/keystore.p12 -jar ./target/https-app-0.0.1-SNAPSHOT.jar
or
> java -Dserver.ssl.key-store-password=password -Dserver.ssl.key-store=/home/foo/keystore.jks -jar ./target/http-app-0.0.1-SNAPSHOT.jar


Access greeting, via https (test server):
> https://localhost:8443/greeting?name=Bob



# Keystores

Populate keystore with public key, required for client access to the https service.

## PKDS12 Keystores:
Export public key from server keystore.p12 , for use by client - create server-public-key.crt:
keytool -exportcert -alias tomcat -file server-public-key.crt -keystore keystore.p12 -storepass password


Import server public cert/key (server-public-key.crt) into a client trust store - create client-truststore.p12:
keytool -importcert -alias server-public-key -keyalg RSA -storetype PKCS12 -keystore client-truststore.p12  -file server-public-key.crt -storepass password -noprompt


## JKS Keystore:
Export public key from keystore.jks , for use by client - create server-public-key.crt:
keytool -exportcert -alias tomcat -file server-public-key.crt -keystore keystore.jks -storepass password


Import server public cert/key (server-public-key.crt) into client trust store - create client-truststore.jks:
keytool -importcert -alias server-public-key -keyalg RSA -storetype PKCS12 -keystore client-truststore.p12  -file server-public-key.crt -storepass password -noprompt


## Get Tomcat/Spring to pick up a custom keystore
After creating another keystore, with the public key, add code to ensure Tomcat/Spring picks it up:
        String certificatesTrustStorePath = "/<mypath>/server-public-key";
        System.setProperty("javax.net.ssl.trustStore", certificatesTrustStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", "password");




## Add certificate to JRE Security cacerts
Alternative just put the certificate into the default cacerts (see below)
keytool -import -v -trustcacerts -alias localhost -file ~/Applications/https-app/server-public-key.crt -keystore ./cacerts -keypass changeit -storepass changeit


# Test Server & Client
Access page via https, which then calls /greeting, via https/ssl (test server & client):
> https://localhost:8443/hello





# Alternatively, create a Certificate Authority, using openssl

(https://deliciousbrains.com/ssl-certificate-authority-for-local-https-development/)

Generate a private key:
> openssl genrsa -des3 -out myCA-private.key 2048

Generate a root certificate (A certificate normally contains the public key, plus purpose of cert, issuer, etc.):
> openssl req -x509 -new -nodes -key myCA-private.key -sha256 -days 1825 -out myCA-RootCert.pem

Place root certificate on all client hosts that will be within your private network. I.e. hosts that will be making
requests into servers. E.g. a front-end microservice that will call a back-end microservice.


## Create CA-signed certificates for server hosts.

Create a private key for a host.
> openssl genrsa -out dev.mydomain.com.key 2048

Create a CSR (Certificate Signing Request), for the new private key:
> openssl req -new -key dev.mydomain.com.key -out dev.mergebot.com.csr

Create the certificate using the CSR, the CA private key, the CA certificate, and a config file (dev.mydomain.com.ext):
> openssl x509 -req -in dev.mydomain.com.csr -CA myCA.pem -CAkey myCA.key -CAcreateserial \
>  -out dev.mydomain.com.crt -days 1825 -sha256 -extfile dev.mydomain.com.ext


The crt and key can then be used to create a jks or p12 file, for use by the Tomcat/Spring.
All clients with the CA root certificate installed will then trust this server certificate.




More about Swarm certs at:
http://cicd.life/generate-ssl-for-docker-swarm/