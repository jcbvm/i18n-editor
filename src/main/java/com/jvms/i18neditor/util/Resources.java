package com.jvms.i18neditor.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jvms.i18neditor.Resource;
import com.jvms.i18neditor.ResourceType;

/**
 * This class provides utility functions for a {@link Resource}.
 * 
 * @author Jacob van Mourik
 */
public final class Resources {
	private final static Charset UTF8_ENCODING = Charset.forName("UTF-8");
	private final static String LOCALE_REGEX = "[a-z]{2}(_[A-Z]{2})?";
	
	/**
	 * Gets all resources from the given {@code rootDir} directory path.
	 * 
	 * <p>The {@code baseName} is the base of the filename of the resource files to look for.<br>
	 * The base name is without extension and without any locale information.<br>
	 * When a resource type is given, only resources of that type will returned.</p>
	 * 
	 * <p>This function will not load the contents of the file, only its description.<br>
	 * If you want to load the contents, use {@link #load(Resource)} afterwards.</p>
	 * 
	 * @param 	rootDir the root directory of the resources
	 * @param 	baseName the base name of the resource files to look for
	 * @param 	type the type of the resource files to look for
	 * @return	list of found resources
	 * @throws 	IOException if an I/O error occurs reading the directory.
	 */
	public static List<Resource> get(Path rootDir, String baseName, Optional<ResourceType> type) throws IOException {
		List<Resource> result = Lists.newLinkedList();
		List<Path> files = Files.walk(rootDir, 1).collect(Collectors.toList());
		for (Path p : files) {
			ResourceType resourceType = null;
			for (ResourceType t : ResourceType.values()) {
				if (isResourceType(type, t) && isResource(rootDir, p, t, baseName)) {
					resourceType = t;
					break;
				}
			}
			if (resourceType != null) {
				String fileName = p.getFileName().toString();
				String extension = resourceType.getExtension();
				Locale locale = null;
				Path path = null;
				if (resourceType.isEmbedLocale()) {
					String pattern = "^" + baseName + "_(" + LOCALE_REGEX + ")" + extension + "$";
					Matcher match = Pattern.compile(pattern).matcher(fileName);
					if (match.find()) {
						locale = LocaleUtils.toLocale(match.group(1));
					}
					path = Paths.get(rootDir.toString(), baseName + (locale == null ? "" : "_" + locale.toString()) + extension);				
				} else {
					locale = LocaleUtils.toLocale(fileName);
					path = Paths.get(rootDir.toString(), locale.toString(), baseName + extension);			
				}
				result.add(new Resource(resourceType, path, locale));
			}
		};
		return result;
	}
	
	/**
	 * Loads the translations of a {@link Resource} from disk.
	 * 
	 * @param 	resource the resource.
	 * @throws 	IOException if an I/O error occurs reading the file.
	 */
	public static void load(Resource resource) throws IOException {
		ResourceType type = resource.getType();
		Path path = resource.getPath();
		SortedMap<String,String> translations;
		if (type == ResourceType.Properties) {
			ExtendedProperties content = new ExtendedProperties();
			content.load(path);
			translations = fromProperties(content);
		} else {
			String content = Files.lines(path, UTF8_ENCODING).collect(Collectors.joining());
			if (type == ResourceType.ES6) {
				content = es6ToJson(content);
			}
			translations = fromJson(content);
		}
		resource.setTranslations(translations);
	}
	
	/**
	 * Writes the translations of the given resource to disk.
	 * 
	 * @param 	resource the resource to write.
	 * @param   prettyPrinting whether to pretty print the contents
	 * @throws 	IOException if an I/O error occurs writing the file.
	 */
	public static void write(Resource resource, boolean prettyPrinting) throws IOException {
		ResourceType type = resource.getType();
		if (type == ResourceType.Properties) {
			ExtendedProperties content = toProperties(resource.getTranslations());
			content.store(resource.getPath());
		} else {
			String content = toJson(resource.getTranslations(), prettyPrinting);
			if (type == ResourceType.ES6) {
				content = jsonToEs6(content);
			}
			if (!Files.exists(resource.getPath())) {
				Files.createDirectories(resource.getPath().getParent());
				Files.createFile(resource.getPath());
			}
			Files.write(resource.getPath(), Lists.newArrayList(content), UTF8_ENCODING);
		}
	}
	
	/**
	 * Creates a new {@link Resource} with the given {@link ResourceType} in the given directory path.
	 * This function should be used to create new resources. For creating an instance of an
	 * existing resource on disk, see {@link #read(Path)}.
	 * 
	 * @param 	type the type of the resource to create.
	 * @param 	root the root directory to write the resource to.
	 * @return	The newly created resource.
	 * @throws 	IOException if an I/O error occurs writing the file.
	 */
	public static Resource create(Path root, ResourceType type, Optional<Locale> locale, String baseName) throws IOException {
		String extension = type.getExtension();
		Path path;
		if (type.isEmbedLocale()) {
			path = Paths.get(root.toString(), baseName + (locale.isPresent() ? "_" + locale.get().toString() : "") + extension);				
		} else {
			path = Paths.get(root.toString(), locale.get().toString(), baseName + extension);			
		}
		Resource resource = new Resource(type, path, locale.orElse(null));
		write(resource, false);
		return resource;
	}
	
	private static boolean isResource(Path root, Path path, ResourceType type, String baseName) throws IOException {
		String extension = type.getExtension();
		Path parent = path.getParent();
		if (parent == null || Files.isSameFile(root, path) || !Files.isSameFile(root, parent)) {
			return false;
		} else if (type.isEmbedLocale()) {
			return Files.isRegularFile(path) &&
					Pattern.matches("^" + baseName + "(_" + LOCALE_REGEX + ")?" + extension + "$", path.getFileName().toString());			
		} else {
			return Files.isDirectory(path) &&
					Pattern.matches("^" + LOCALE_REGEX + "$", path.getFileName().toString()) &&
					Files.isRegularFile(Paths.get(path.toString(), baseName + extension));
		}
	}
	
	private static boolean isResourceType(Optional<ResourceType> a, ResourceType b) {
		return !a.isPresent() || a.get() == b;
	}
	
	private static SortedMap<String,String> fromProperties(ExtendedProperties properties) {
		SortedMap<String,String> result = Maps.newTreeMap();
		properties.forEach((key, value) -> {
			result.put((String)key, StringEscapeUtils.unescapeJava((String)value));
		});
		return result;
	}
	
	private static ExtendedProperties toProperties(Map<String,String> translations) {
		ExtendedProperties result = new ExtendedProperties();
		result.putAll(translations);
		return result;
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
				String newKey = key == null ? entry.getKey() : ResourceKeys.create(key, entry.getKey());
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
	
	private static String toJson(Map<String,String> translations, boolean prettify) {
		List<String> keys = Lists.newArrayList(translations.keySet());
		JsonElement elem = toJson(translations, null, keys);
		GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();
		if (prettify) {
			builder.setPrettyPrinting();
		}
		return builder.create().toJson(elem);
	}
	
	private static JsonElement toJson(Map<String,String> translations, String key, List<String> keys) {
		if (keys.size() > 0) {
			JsonObject object = new JsonObject();
			ResourceKeys.uniqueRootKeys(keys).forEach(rootKey -> {
				String subKey = ResourceKeys.create(key, rootKey);
				List<String> subKeys = ResourceKeys.extractChildKeys(keys, rootKey);
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
