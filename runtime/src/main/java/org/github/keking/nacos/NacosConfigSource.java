package org.github.keking.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import org.eclipse.microprofile.config.spi.ConfigSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kl : http://kailing.pub
 * @version 1.0
 * @date 2020/9/18
 */
public class NacosConfigSource implements ConfigSource {
    private static final String NAME_FORMAT = "NacosConfigSource[%s]";
    private static final Integer GET_CONFIG_TIMEOUT = 1000 * 60; //60s
    private final String nacosAppId;
    private final String nacosGroup;
    private final NacosConfig.Type nacosType;
    private final ConfigService configService;
    private static final int ORDINAL = 270; // 配置加载优先级高于文件系统或jar常规，但低于env vars
    private final Map<String, String> configMap = new ConcurrentHashMap<>();

    NacosConfigSource(ConfigService configService, NacosConfig nacosConfig) {
        this.nacosAppId = nacosConfig.appId.get();
        this.nacosGroup = nacosConfig.group.get();
        this.nacosType = nacosConfig.type.get();
        this.configService = configService;
        this.configMap.putAll(this.initNacosConfig());
        this.startConfigListener();
    }

    /**
     * 配置监听
     */
    private void startConfigListener() {
        try {
            configService.addListener(nacosAppId, nacosGroup, new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    configMap.putAll(NacosUtils.stringToMap(configInfo,nacosType));
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getOrdinal() {
        return ORDINAL;
    }

    private Map<String, String> initNacosConfig() {
        try {
            String context = configService.getConfig(nacosAppId, nacosGroup, GET_CONFIG_TIMEOUT);
            return NacosUtils.stringToMap(context, nacosType);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    @Override
    public Map<String, String> getProperties() {
        return configMap;
    }

    @Override
    public String getValue(String propertyName) {
        return configMap.get(propertyName);
    }

    @Override
    public String getName() {
        return String.format(NAME_FORMAT, nacosAppId);
    }
}