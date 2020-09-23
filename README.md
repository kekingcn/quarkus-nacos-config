# quarkus-nacos-config
quarkus framework extension of nacos configuration center

## Quick start
- 1、Introduce maven coordinates
```
   <dependency>
     <groupId>org.github.keking</groupId>
     <artifactId>quarkus-nacos-config-ext</artifactId>
     <version>1.0-SNAPSHOT</version>
   </dependency>
```
- 2、Add the following configuration in the application.properties file
```
#nacos
quarkus.nacos=true
quarkus.nacos.app-id=nacosApp
quarkus.nacos.type=properties
quarkus.nacos.group=DEFAULT_GROUP
quarkus.nacos.server-addr=127.0.0.1:8848
```
By default, the switch of nacos extension is turned off, and you need to use the quarkus.nacos configuration to manually turn it on.quarkus.nacos.server-addr can be overridden by -Dnacos.serverAddr=xx parameter at runtime

## Quarkus Config Use
- 1、How to configure the class
```
@ConfigProperties(prefix = "quarkus.app")
public class QuarkusConfig {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```
- 2、Property injection method
```
@Singleton
@Startup
public class ConfigService {

    @ConfigProperty(name = "quarkus.app.name")
    String appName;
    
    public void print(){
        System.out.println(appName);
    }
}
```
-3、Api manual acquisition method
```
@Singleton
@Startup
public class ConfigService {

    public void print(){
        Config config = ConfigProvider.getConfig();
        System.out.println(config.getValue("quarkus.app.name",String.class));
    }
}
```
## Other resources
- nacos : https://nacos.io
- quarkus: https://github.com/quarkusio/quarkus
- klblog : http://www.kailing.pub/