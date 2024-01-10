/*
 * Copyright 2023-2024 Dmitry Chubrick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.introfog.gitwave.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UpdateChecker {
    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("\"name\":\\s?\"([^\"]+)\"");
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateChecker.class);

    private UpdateChecker() {
        // Do nothing
    }

    public static boolean isNewReleaseAvailable() {
        // TODO MINOR fetch and check update in the separate thread, to quicker open app.
        final String latestTag = fetchLatestTag(AppConstants.GIT_HUB_REPO_OWNER, AppConstants.APP_NAME);
        LOGGER.info("Fetch '{}' tag from GitHub repo.", latestTag);
        return latestTag != null && !AppConstants.VERSION.equals(latestTag);
    }

    public static String fetchLatestTag(String owner, String repo) {
        final String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/tags";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("User-Agent", "GitWave/" + AppConstants.VERSION + " (Java; en-US)");
            connection.connect();

            if (connection.getResponseCode() != 200) {
                LOGGER.warn("Failed to fetch tags from GitHub API: code='{}', body='{}'",
                        connection.getResponseCode(), connection.getResponseMessage());
                return null;
            }

            String responseStr = inputStreamToString(connection.getInputStream());
            connection.disconnect();
            // Use here regex on purpose, to not increase size of the app by adding huge library for one request.
            Matcher matcher = TAG_NAME_PATTERN.matcher(responseStr);

            if (matcher.find()) {
                return matcher.group(1);
            }
            return null;

        } catch (Exception e) {
            LOGGER.warn("Error fetching latest version from GitHub", e);
            return null;
        }
    }

    private static String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();
        return result.toString();
    }
}
