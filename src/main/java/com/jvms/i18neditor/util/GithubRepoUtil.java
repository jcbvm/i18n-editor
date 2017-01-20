package com.jvms.i18neditor.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * This class provides utility functions for retrieving Github repo data.
 * 
 * @author Jacob
 */
public final class GithubRepoUtil {
	private final static Gson gson = new Gson();
	private final static ExecutorService executor;
	
	static {
		executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
				.setNameFormat("github-repo-pool-%d")
				.build());
	}
	
	/**
	 * Gets the latest release data of a Github repo.
	 * 
	 * @param 	repo the Github repo to get the latest release data from.
	 * @return	the latest Github release data.
	 */
	public static Future<GithubReleaseData> getLatestRelease(String repo) {
		return executor.submit(() -> {
			HttpURLConnection connection = null;
			URL url = new URL("https://api.github.com/repos/" + repo + "/releases/latest");
			try {
				connection = (HttpURLConnection)url.openConnection();
				try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), Charsets.UTF_8)) {
					return gson.fromJson(reader, GithubReleaseData.class);
				}
			} catch (IOException e) {
				return null;
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		});
	}
	
	/**
	 * This class represents Github release data.
	 */
	public final class GithubReleaseData {
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
