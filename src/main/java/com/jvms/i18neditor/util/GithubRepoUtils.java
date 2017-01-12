package com.jvms.i18neditor.util;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * This class provides utility functions for retrieving Github repo data.
 * 
 * @author Jacob
 */
public final class GithubRepoUtils {
	private final static Gson gson = new Gson();
	private final static ExecutorService executor = Executors.newCachedThreadPool();
	
	public static Future<GithubReleaseData> getLatestRelease(String repo) {
		return executor.submit(() -> {
			HttpURLConnection connection = null;
			URL url = new URL("https://api.github.com/repos/" + repo + "/releases/latest");
			try {
				connection = (HttpURLConnection)url.openConnection();
				try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), Charsets.UTF_8)) {
					return gson.fromJson(reader, GithubReleaseData.class);
				}
			} catch (Exception e) {
				return null;
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		});
	}
	
	public class GithubReleaseData {
		@SerializedName("tag_name")
		private String tagName;
		@SerializedName("html_url")
		private String htmlUrl;
		
		public String getTagName() {
			return tagName;
		}
		
		public String getHtmlUrl() {
			return htmlUrl;
		}
	}
}
