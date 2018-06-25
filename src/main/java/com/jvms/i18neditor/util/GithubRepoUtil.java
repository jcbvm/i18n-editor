package com.jvms.i18neditor.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * This class provides utility functions for retrieving Github Repository data.
 * 
 * @author Jacob van Mourik
 */
public final class GithubRepoUtil {
	private final static Logger log = LoggerFactory.getLogger(GithubRepoUtil.class);
	private final static Gson gson = new Gson();
	private final static ExecutorService executor;
	
	static {
		executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
				.setNameFormat("github-repo-util-pool-%d")
				.build());
	}
	
	/**
	 * Gets the full Github Repository URL.
	 * 
	* @param 	username the Github Repository user name.
	 * @param 	project the Github Repository project name.
	 * @return	the full Github Repository URL.
	 */
	public static String getURL(String username, String project) {
		return "https://github.com/" + username + "/" + project;
	}
	
	/**
	 * Gets the latest release data of a Github Repository.
	 * 
	 * @param 	username the Github Repository user name.
	 * @param 	project the Github Repository project name.
	 * @return	the latest Github Repository release data.
	 */
	public static Future<GithubRepoReleaseData> getLatestRelease(String username, String project) {
		return executor.submit(() -> {
			HttpURLConnection connection = null;
			URL url = new URL("https://api.github.com/repos/" + username + "/" + project + "/releases/latest");
			try {
				connection = (HttpURLConnection)url.openConnection();
				try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), Charsets.UTF_8)) {
					return gson.fromJson(reader, GithubRepoReleaseData.class);
				}
			} catch (IOException e) {
				log.error("Unable to retrieve latest github release data", e);
				return null;
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		});
	}
	
	/**
	 * This class represents Github Repository release data.
	 */
	public final class GithubRepoReleaseData {
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
