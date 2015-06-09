package com.jvms.i18neditor.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jvms.i18neditor.Resource;
import com.jvms.i18neditor.Resource.ResourceType;

public final class Resources {
	public final static String LOCALE_REGEX = "[a-z]{2}(_[a-z]{2})?";
	public final static Charset DEFAULT_ENCODING = Charset.forName("UTF-8"); 
	
	public static boolean isResource(Path path) {
		return isJsonResource(path) || isEs6Resource(path);
	}
	
	public static boolean isJsonResource(Path path) {
		return Files.isDirectory(path) 
				&& Pattern.matches("^(?i:" + LOCALE_REGEX + ")$", path.getFileName().toString())
				&& Files.isRegularFile(Paths.get(path.toString(), "translations.json"));
	}
	
	public static boolean isEs6Resource(Path path) {
		return Files.isDirectory(path) 
				&& Pattern.matches("^(?i:" + LOCALE_REGEX + ")$", path.getFileName().toString())
				&& Files.isRegularFile(Paths.get(path.toString(), "translations.js"));
	}
	
	public static Resource read(Path path) throws IOException {
		if (!isResource(path)) return null;
		ResourceType type;
		Path filePath;
		if (isEs6Resource(path)) {
			type = ResourceType.ES6;
			filePath = Paths.get(path.toString(), "translations.js");
		} else {
			type = ResourceType.JSON;
			filePath = Paths.get(path.toString(), "translations.json");
		}
		String content = Files.lines(filePath, DEFAULT_ENCODING).collect(Collectors.joining());
		if (type == ResourceType.ES6) {
			content = es6ToJson(content);
		}
		Locale locale = parseLocale(path.getFileName().toString());
		return new Resource(type, filePath, locale, fromJson(content));
	}
	
	public static void write(Resource resource) throws IOException {
		String content = toJson(resource.getTranslations());
		if (resource.getType() == ResourceType.ES6) {
			content = jsonToEs6(content);
		}
		if (!Files.exists(resource.getPath())) {
			Files.createDirectories(resource.getPath().getParent());
			Files.createFile(resource.getPath());
		}
		Files.write(resource.getPath(), Lists.newArrayList(content), DEFAULT_ENCODING);
	}
	
	public static Resource create(ResourceType type, Path path) throws IOException {
		Path filePath = Paths.get(path.toString(), "translations.json");
		Locale locale = parseLocale(path.getFileName().toString());
		Resource resource = new Resource(ResourceType.JSON, filePath, locale);
		write(resource);
		return resource;
	}
	
	private static Locale parseLocale(String locale) {
		String[] localeParts = locale.split("_");
		if (localeParts.length > 1) {
			return new Locale(localeParts[0], localeParts[1]);
		} else {
			return new Locale(localeParts[0]);
		}
	}
	
	private static SortedMap<String,String> fromJson(String json) {
		SortedMap<String,String> result = Maps.newTreeMap();
		JsonElement elem = new JsonParser().parse(json);
		fromJson(null, elem, result);
		return result;
	}
	
	private static void fromJson(String key, JsonElement elem, Map<String,String> content) {
		if (elem.isJsonObject()) {
			elem.getAsJsonObject().entrySet().forEach(entry -> {
				String newKey = key == null ? entry.getKey() : TranslationKeys.create(key, entry.getKey());
				fromJson(newKey, entry.getValue(), content);
			});
		} else if (elem.isJsonPrimitive()) {
			content.put(key, StringEscapeUtils.unescapeJava(elem.getAsString()));
		} else if (elem.isJsonNull()) {
			content.put(key, "");
		} else {
			throw new IllegalArgumentException("Found invalid json element.");
		}
	}
	
	private static String toJson(Map<String,String> translations) {
		List<String> keys = Lists.newArrayList(translations.keySet());
		JsonElement elem = toJson(translations, null, keys);
		return elem.toString();
	}
	
	private static JsonElement toJson(Map<String,String> translations, String key, List<String> keys) {
		if (keys.size() > 0) {
			JsonObject object = new JsonObject();
			TranslationKeys.uniqueRootKeys(keys).forEach(rootKey -> {
				String subKey = TranslationKeys.create(key, rootKey);
				List<String> subKeys = TranslationKeys.extractChildKeys(keys, rootKey);
				object.add(rootKey, toJson(translations, subKey, subKeys));
			});
			return object;
		}
		if (key == null) {
			return new JsonObject();
		}
		return new JsonPrimitive(translations.get(key));
	}
	
	private static String es6ToJson(String content) {
		return content.replaceAll("export +default", "").replaceAll("} *;", "}");
	}
	
	private static String jsonToEs6(String content) {
		return "export default " + content + ";";
	}
}
