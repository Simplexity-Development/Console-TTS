package simplexity.config;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored", "unused"})
public class YmlConfig {

    private final File file;
    private Map<String, Object> root;
    private final Logger logger = LoggerFactory.getLogger(YmlConfig.class);


    /**
     * Obtains the YML config
     *
     * @param file File to read from
     * @throws IOException exception if file is not found or is invalid for some reason
     */

    public YmlConfig(File file, String path) throws IOException {
        this.file = file;
        this.root = new LinkedHashMap<>();
        saveDefaultConfigFromResources(path, file);
        reloadConfig();
        mergeDefaults(path);
        saveConfig();
    }

    public YmlConfig(Map<String, Object> root) {
        this.file = null;
        this.root = root;
    }

    /**
     * Retrieves a value from the config and casts it to the given type, or returns a default if not found or of wrong type.
     *
     * @param path         The dot-separated config path - i.e. "section.option"
     * @param type         The expected type of the value - i.e. Integer.class, String.class
     * @param defaultValue The value to return if the path is missing or the type does not match
     * @param logErrors    Whether to log errors about null items or incorrect item types
     * @param <T>          The return type
     * @return The cast value if present and valid, otherwise the default
     */

    @Nullable
    public <T> T getOption(String path, Class<T> type, @Nullable T defaultValue, boolean logErrors) {
        Object value = getValue(path);
        if (type.isInstance(value)) return type.cast(value);
        if (defaultValue != null) setDefault(path, defaultValue);
        if (logErrors) {
            if (value == null)
                logger.warn("[Config] Missing config value at '{}', using default: '{}'", path, defaultValue);
        } else {
            logger.warn("[Config] Config value at '{}' is the incorrect type. Type expected: {}", path, type.getName());
        }
        return defaultValue;
    }


    /**
     * @see #getOption(String, Class, Object, boolean)
     */

    public <T> T getOption(String path, Class<T> type, @Nullable T defaultValue) {
        return getOption(path, type, defaultValue, true);
    }


    /**
     * @see #getOption(String, Class, Object, boolean)
     */

    public <T> T getOption(String path, Class<T> type, boolean logErrors) {
        return getOption(path, type, null, logErrors);
    }

    /**
     * @see #getOption(String, Class, Object, boolean)
     */

    public <T> T getOption(String path, Class<T> type) {
        return getOption(path, type, null, true);
    }

    /**
     * Retrieves a typed List from the config at the specified path.
     * If the path is missing or the list contains elements not matching the given type,
     * only valid typed elements will be returned. If the path isn't a list, this returns an empty list.
     *
     * @param path        The dot-separated config path i.e. "settings.flags"
     * @param type        The expected element type i.e. String.class, Integer.class
     * @param defaultList List to return if no list was found
     * @param logErrors   Whether to log errors about null items or incorrect item types
     * @param <T>         The element type
     * @return A list of elements of the given type, or an empty list if not found or mismatched
     */

    public <T> List<T> getList(String path, Class<T> type, List<T> defaultList, boolean logErrors) {
        Object value = getValue(path);
        if (value instanceof List<?> rawList) {
            List<T> result = new ArrayList<>();
            for (Object item : rawList) {
                if (type.isInstance(item)) {
                    result.add(type.cast(item));
                } else if (logErrors) {
                    logger.warn("[Config] List item '{}' at '{}' is the incorrect type. Type expected: {}", item, path, type.getName());
                }
            }
            return result;
        }
        if (defaultList != null) setDefault(path, defaultList);
        if (logErrors) {
            logger.warn("[Config] List at '{}' is null. Using default: {}", path, defaultList);
        }
        return defaultList;
    }

    /**
     * @see #getList(String, Class, List, boolean)
     */

    public <T> List<T> getList(String path, Class<T> type, List<T> defaultList) {
        return getList(path, type, defaultList, true);
    }

    /**
     * @see #getList(String, Class, List, boolean)
     */

    public <T> List<T> getList(String path, Class<T> type, boolean logErrors) {
        return getList(path, type, null, logErrors);
    }

    /**
     * @see #getList(String, Class, List, boolean)
     */

    public <T> List<T> getList(String path, Class<T> type) {
        return getList(path, type, null, true);
    }

    /**
     * Returns a piece of a config as a separate config.
     *
     * @param path Path to the section - i.e. 'option.sub-option'
     * @return YmlConfig config section
     */

    @Nullable
    public YmlConfig getConfigSection(String path, boolean logErrors) {
        Object value = getValue(path);
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> sectionMap = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String key) {
                    sectionMap.put(key, entry.getValue());
                }
            }
            return new YmlConfig(sectionMap);
        }
        if (logErrors) {
            logger.warn("[Config] No configuration section found in '{}'", path);
        }
        return null;
    }

    /**
     * @see #getConfigSection(String, boolean)
     */

    public YmlConfig getConfigSection(String path) {
        return getConfigSection(path, true);
    }

    public Set<String> getKeys(String path) {
        Object section = getValue(path);
        if (section instanceof Map<?, ?> map) {
            Set<String> keys = new HashSet<>();
            for (Object key : map.keySet()) {
                if (key instanceof String s) keys.add(s);
            }
            return keys;
        }
        return Collections.emptySet();
    }


    /**
     * Retrieves a strongly-typed Map from the configuration at the specified path.
     * This method attempts to cast both the keys and values of the map at the given path
     * to the provided keyType and valueType. Only entries that match the
     * expected types will be included in the returned map. All other entries are ignored.
     *
     * @param path       The dot-separated path to the target section - i.e. 'option.sub-option'
     * @param keyType    The expected class type of the map keys - usually a String.class
     * @param valueType  The expected class type of the map values - i.e. String.class, Integer.class etc
     * @param defaultMap The map to return if no map is found
     * @param <K>        The type of the map keys.
     * @param <V>        The type of the map values.
     * @param logErrors  Whether errors should be logged about null values or invalid types
     * @return A LinkedHashMap of the matching key/value pairs, or null if the path does not point to a map.
     */

    @Nullable
    public <K, V> Map<K, V> getHashMap(String path, @Nullable HashMap<K, V> defaultMap, Class<K> keyType, Class<V> valueType, boolean logErrors) {
        Object value = getValue(path);
        if (value instanceof Map<?, ?> rawMap) {
            Map<K, V> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                Object key = entry.getKey();
                Object val = entry.getValue();

                boolean validKey = keyType.isInstance(key);
                boolean validVal = valueType.isInstance(val);

                if (validKey && validVal) {
                    result.put(keyType.cast(key), valueType.cast(val));
                } else if (logErrors) {
                    String keyString = (key != null ? key.toString() : "null");
                    String valString = (val != null ? val.toString() : "null");
                    logger.warn("[Config] Skipped invalid map entry at '{}', key={}, value={}, " + "expected key type={}, expected value type={}", path, keyString, valString, keyType.getName(), valueType.getName());
                }
            }
            return result;
        }
        if (defaultMap != null) setDefault(path, defaultMap);
        if (logErrors) {
            if (value == null) {
                logger.warn("[Config] Missing map at '{}', using default map: {}", path, defaultMap);
            } else {
                logger.warn("[Config] Expected map at '{}' but got '{}' instead. Using default map: {}", path, value.getClass().getName(), defaultMap);
            }
        }
        return defaultMap;
    }

    /**
     * @see #getHashMap(String, HashMap, Class, Class, boolean)
     */

    public <K, V> Map<K, V> getHashMap(String path, @Nullable HashMap<K, V> defaultMap, Class<K> keyType, Class<V> valueType) {
        return getHashMap(path, defaultMap, keyType, valueType, true);
    }

    /**
     * @see #getHashMap(String, HashMap, Class, Class, boolean)
     */

    public <K, V> Map<K, V> getHashMap(String path, Class<K> keyType, Class<V> valueType, boolean logErrors) {
        return getHashMap(path, null, keyType, valueType, logErrors);
    }

    /**
     * @see #getHashMap(String, HashMap, Class, Class, boolean)
     */

    public <K, V> Map<K, V> getHashMap(String path, Class<K> keyType, Class<V> valueType) {
        return getHashMap(path, null, keyType, valueType, true);
    }


    public void reloadConfig() throws IOException {
        try (FileInputStream input = new FileInputStream(file)) {
            Object loadedConfig = new Yaml().load(input);
            if (loadedConfig instanceof Map<?, ?> map) {
                this.root = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() instanceof String key) {
                        this.root.put(key, entry.getValue());
                    }
                }
            } else {
                this.root = new LinkedHashMap<>();
            }
        }
    }

    public void saveConfig() throws IOException {
        if (file == null) throw new IllegalStateException("Cannot save config if there's not a file to reference");
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(2);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setSplitLines(false);
        options.setAllowUnicode(true);

        Yaml yml = new Yaml(options);

        try (FileWriter writer = new FileWriter(file)) {
            yml.dump(this.root, writer);
        }
    }

    private Object getValue(String path) {
        String[] pathParts = path.split("\\.");
        Object current = root;
        for (String part : pathParts) {
            if (current instanceof Map<?, ?> map) {
                current = map.get(part);
            } else {
                return null;
            }
        }
        return current;
    }

    private void setValue(String path, Object value) {
        String[] pathParts = path.split("\\.");
        Map<String, Object> current = root;
        for (int i = 0; i < pathParts.length - 1; i++) {
            Object next = current.get(pathParts[i]);
            if (!(next instanceof Map)) {
                next = new LinkedHashMap<String, Object>();
                current.put(pathParts[i], next);
            }
            current = (Map<String, Object>) next;
        }
        current.put(pathParts[pathParts.length - 1], value);
    }

    public void setDefault(String path, Object defaultValue) {
        if (getValue(path) == null) {
            setValue(path, defaultValue);
        }
    }


    private void saveDefaultConfigFromResources(String resourcePath, File destination) throws IOException {
        if (!destination.exists()) {
            destination.getParentFile().mkdirs();
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                if (in == null) throw new FileNotFoundException("Resource not found: " + resourcePath);
                Files.copy(in, destination.toPath());
            }
        }
    }

    private void mergeDefaults(String resourcePath) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) return;
            Yaml yml = new Yaml();
            Object loadedYml = yml.load(in);
            if (loadedYml instanceof Map<?, ?> defaults) {
                mergeMaps(this.root, (Map<String, Object>) defaults);
            }
        }
    }

    private void mergeMaps(Map<String, Object> target, Map<String, Object> defaults) {
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            String key = entry.getKey();
            Object defaultValue = entry.getValue();
            if (!target.containsKey(key)) {
                target.put(key, defaultValue);
            } else if (defaultValue instanceof Map<?, ?> defaultSubMap && target.get(key) instanceof Map<?, ?> targetSubMap) {
                mergeMaps((Map<String, Object>) targetSubMap, (Map<String, Object>) defaultSubMap);
            }
        }
    }
}
