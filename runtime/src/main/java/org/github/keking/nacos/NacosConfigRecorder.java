package org.github.keking.nacos;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.logging.Logger;

import java.util.Collections;

/**
 * @author kl : http://kailing.pub
 * @version 1.0
 * @date 2020/9/18
 */
@Recorder
public class NacosConfigRecorder {

    private static final Logger log = Logger.getLogger(NacosConfigRecorder.class);

    public RuntimeValue<ConfigSourceProvider> configSources(NacosConfig nacosConfig) {
        if (!nacosConfig.enabled) {
            log.info("nacos配置未开启");
            return emptyRuntimeValue();
        }
        log.info("nacos配置已开启");
        return new RuntimeValue<>(new NacosConfigSourceProvider(nacosConfig));
    }

    private RuntimeValue<ConfigSourceProvider> emptyRuntimeValue() {
        return new RuntimeValue<>(new EmptyConfigSourceProvider());
    }

    private static class EmptyConfigSourceProvider implements ConfigSourceProvider {
        @Override
        public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
            return Collections.emptyList();
        }
    }
}
