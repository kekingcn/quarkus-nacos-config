package org.github.keking.nacos;

import io.smallrye.config.common.utils.ConfigSourceUtil;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author kl : http://kailing.pub
 * @version 1.0
 * @date 2020/9/23
 */
public class NacosUtils {

    private NacosUtils(){}

    public static Map<String, String> stringToMap(String str, NacosConfig.Type type){
        Map<String, String> config = new HashMap<>();
        try {
            if (type.equals(NacosConfig.Type.properties)) {
                Properties properties = new Properties();
                StringReader reader = new StringReader(str);
                properties.load(reader);
                config.putAll(ConfigSourceUtil.propertiesToMap(properties));
            }
            if (type.equals(NacosConfig.Type.yaml)) {
                config.putAll(yamlStringToMap(str));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    private static Map<String, String> yamlStringToMap(String str) {
        Map<String, Object> yamlInput = (Map)(new Yaml()).loadAs(str, HashMap.class);
        return yamlInputToMap(yamlInput);
    }

    private static Map<String, String> yamlInputToMap(Map<String, Object> yamlInput) {
        Map<String, String> properties = new TreeMap();
        if (yamlInput != null) {
            flattenYaml("", yamlInput, properties);
        }

        return properties;
    }

    private static void flattenYaml(String path, Map<String, Object> source, Map<String, String> target) {
        source.forEach((key, value) -> {
            if (key != null && key.indexOf(46) != -1) {
                key = "\"" + key + "\"";
            }

            if (key != null && !key.isEmpty() && path != null && !path.isEmpty()) {
                key = path + "." + key;
            } else if (path != null && !path.isEmpty()) {
                key = path;
            } else if (key == null || key.isEmpty()) {
                key = "";
            }

            if (value instanceof String) {
                target.put(key, (String)value);
            } else if (value instanceof Map) {
                flattenYaml(key, (Map)value, target);
            } else if (value instanceof List) {
                List<Object> list = (List)value;
                flattenList(key, list, target);

                for(int i = 0; i < list.size(); ++i) {
                    flattenYaml(key, Collections.singletonMap("[" + i + "]", list.get(i)), target);
                }
            } else {
                target.put(key, value != null ? value.toString() : "");
            }

        });
    }

    private static void flattenList(String key, List<Object> source, Map<String, String> target) {
        if (source.stream().allMatch((o) -> o instanceof String)) {
            target.put(key, source.stream().map((o) -> {
                StringBuilder sb = new StringBuilder();
                escapeCommas(sb, o.toString(), 1);
                return sb.toString();
            }).collect(Collectors.joining(",")));
        } else {
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
            dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.FOLDED);
            target.put(key, (new Yaml(dumperOptions)).dump(Collections.singletonMap(key.substring(key.lastIndexOf(".") + 1), source)));
        }

    }

    private static void escapeCommas(StringBuilder b, String src, int escapeLevel) {
        int cp;
        for(int i = 0; i < src.length(); i += Character.charCount(cp)) {
            cp = src.codePointAt(i);
            if (cp == 92 || cp == 44) {
                for(int j = 0; j < escapeLevel; ++j) {
                    b.append('\\');
                }
            }

            b.appendCodePoint(cp);
        }

    }
}
