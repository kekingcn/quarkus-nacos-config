package org.github.keking.nacos;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

import java.util.Optional;

/**
 * @author kl : http://kailing.pub
 * @version 1.0
 * @date 2020/9/18
 */
@ConfigRoot(name = "nacos", phase = ConfigPhase.BOOTSTRAP)
public class NacosConfig {

    /**
     * 是否开启nacos
     */
    @ConfigItem(name = ConfigItem.PARENT, defaultValue = "false")
    boolean enabled;
    /**
     * appId
     */
    @ConfigItem
    Optional<String> appId;

    /**
     * 配置类型
     */
    @ConfigItem(defaultValue = "properties")
    Optional<Type> type;

    /**
     * group
     */
    @ConfigItem(defaultValue = "DEFAULT_GROUP")
    Optional<String> group;

    /**
     * server地址,也可以通过-Dnacos.serverAddr=127.0.0.1:8848 覆盖application.properties中的配置
     */
    @ConfigItem(defaultValue = "127.0.0.1:8848")
    Optional<String> serverAddr;

    public enum Type{
        properties,
        yaml
    }
}
