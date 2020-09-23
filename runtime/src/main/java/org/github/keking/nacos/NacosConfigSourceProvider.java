package org.github.keking.nacos;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.utils.StringUtils;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author kl : http://kailing.pub
 * @version 1.0
 * @date 2020/9/18
 */
public class NacosConfigSourceProvider implements ConfigSourceProvider {

    private final NacosConfig config;

    private static final String SYSTEM_SERVER_ADDR = "nacos.serverAddr";


    public NacosConfigSourceProvider(NacosConfig config) {
        this.config = config;
    }

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader classLoader) {
        List<ConfigSource> configSources = new ArrayList<>(1);
        String serverAddr = System.getProperty(SYSTEM_SERVER_ADDR);
        if(StringUtils.isEmpty(serverAddr)){
            serverAddr = config.serverAddr.get();
        }
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
        try {
            ConfigService configService = NacosFactory.createConfigService(properties);
            configSources.add(new NacosConfigSource(configService, config));
        } catch (NacosException e) {
            e.printStackTrace();
        }

        return configSources;
    }


}
