/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.service;

import org.cgiar.ccafs.util.NumberParseUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class AltmetricService {

  public final static String ALTMETRICS_API_URL = "https://api.altmetric.com/v1/";
  public final static String ALTMETRICS_API_DOI_URL = ALTMETRICS_API_URL + "doi/";
  public final static String ALTMETRICS_API_ID_URL = ALTMETRICS_API_URL + "id/";

  /**
   * Currently unused.
   * 
   * @param oldDetailUrl the old URL which redirects
   * @return a redirection-free URL
   * @throws IOException if an exception occurs while following the redirections.
   */
  public static String followRedirectDetailUrlAltmetric(String oldDetailUrl) throws IOException {
    String detailUrl = oldDetailUrl;
    // taken from https://stackoverflow.com/a/26046079
    URL resourceUrl, base, next;
    Map<String, Integer> visited;
    HttpURLConnection conn;
    String location;
    int times = 0;

    visited = new HashMap<>();
    while (times < 4) {
      times = visited.compute(detailUrl, (key, count) -> count == null ? 1 : count + 1);

      if (times > 3) {
        throw new IOException("Stuck in redirect loop");
      }

      resourceUrl = new URL(detailUrl);
      conn = (HttpURLConnection) resourceUrl.openConnection();

      conn.setConnectTimeout(15000);
      conn.setReadTimeout(15000);
      conn.setInstanceFollowRedirects(false); // Make the logic below easier to detect redirections
      conn.setRequestProperty("User-Agent", "Mozilla/5.0...");

      switch (conn.getResponseCode()) {
        case HttpURLConnection.HTTP_MOVED_PERM:
        case HttpURLConnection.HTTP_MOVED_TEMP:
          location = conn.getHeaderField("Location");
          location = URLDecoder.decode(location, "UTF-8");
          base = new URL(detailUrl);
          next = new URL(base, location); // Deal with relative URLs
          detailUrl = next.toExternalForm();
          continue; // another redirection, start again...
      }

      break; // apparently there is no more redirections, break out of cycle.
    }

    return detailUrl;
  }

  public static String formatDetailUrlAltmetric(String oldDetailUrl) {
    String detailUrl = StringUtils.stripToEmpty(oldDetailUrl);
    if (detailUrl.isEmpty()) {
      return StringUtils.EMPTY;
    }

    if (detailUrl.contains("details.php")) {
      detailUrl = detailUrl.replace(".php?citation_id=", "/");
    }

    return detailUrl;
  }

  /**
   * <p>
   * Reads an Altmetric article by its DOI name.
   * </p>
   * <p>
   * A DOI name is the path part of a DOI URL. E.g. if your DOI URL is
   * {@code https://www.doi.org/10.1007/978-3-319-29794-1_9}, you should enter {@code 10.1007/978-3-319-29794-1_9}.
   * </p>
   * 
   * @param doi the DOI name
   * @return a JSON with the response; null if the DOI was not found.
   * @throws IOException if an exception occurs when reading from the API.
   */
  public static JsonElement readDoiFromAltmetric(final String doi) throws IOException {
    URL altmetricURL = new URL(ALTMETRICS_API_DOI_URL + doi);
    JsonElement element = null;

    try (InputStreamReader reader = new InputStreamReader(altmetricURL.openStream())) {
      element = JsonParser.parseReader(reader);
    } catch (FileNotFoundException fnfe) {
      element = JsonNull.INSTANCE;
    }

    return element;
  }

  public static List<JsonElement> readDoisFromAltmetric(final List<String> dois)
    throws IOException, UnsupportedEncodingException {
    List<JsonElement> responses = new ArrayList<>();
    for (String doi : dois) {
      URL altmetricURL = new URL(ALTMETRICS_API_DOI_URL + doi);
      InputStreamReader reader = null;
      try {
        reader = new InputStreamReader(altmetricURL.openStream());
      } catch (FileNotFoundException fnfe) {
      }

      JsonElement element = reader != null ? JsonParser.parseReader(reader) : null;
      responses.add(element);
    }

    responses.removeIf(Objects::isNull);

    return responses;
  }

  /**
   * <p>
   * Reads an Altmetric article by its ID.
   * </p>
   * <p>
   * Note that according to the <a href = "https://api.altmetric.com/docs/call_id.html"> API documentation</a>, this way
   * to find an Altmetric article is not safe because "[...] these IDs are unstable over the medium term, you're much
   * better off using a DOI [...] if you're able."
   * </p>
   * 
   * @param altmetricId the Altmetric article ID
   * @return a JSON with the response; null if the ID was not found.
   * @throws IOException if an exception occurs when reading from the API.
   */
  public static JsonElement readIdFromAltmetric(final long altmetricId) throws IOException {
    URL altmetricURL = new URL(ALTMETRICS_API_ID_URL + altmetricId);
    JsonElement element = null;

    try (InputStreamReader reader = new InputStreamReader(altmetricURL.openStream())) {
      element = JsonParser.parseReader(reader);
    } catch (FileNotFoundException fnfe) {
      element = JsonNull.INSTANCE;
    }

    return element;
  }

  public static List<JsonElement> readIdsFromAltmetric(final List<Integer> ids)
    throws IOException, UnsupportedEncodingException {
    List<JsonElement> responses = new ArrayList<>();

    for (Integer id : ids) {
      URL altmetricURL = new URL(ALTMETRICS_API_ID_URL + id);
      InputStreamReader reader = null;
      try {
        reader = new InputStreamReader(altmetricURL.openStream());
      } catch (FileNotFoundException fnfe) {
      }

      JsonElement element = reader != null ? JsonParser.parseReader(reader) : JsonNull.INSTANCE;
      responses.add(element);
    }

    return responses;
  }

  public static Long tryGetAltmetricId(final String altmetricUrl) {
    if (StringUtils.isBlank(altmetricUrl)) {
      return null;
    }

    long possibleId = -0L;
    String possibleIdString = altmetricUrl;
    if (StringUtils.contains(possibleIdString, "citation_id")) {
      possibleIdString = StringUtils.substringAfter(possibleIdString, "citation_id=");
    } else if (StringUtils.contains(possibleIdString, "details/")) {
      possibleIdString = StringUtils.substringAfter(possibleIdString, "details/");
    }

    if (!StringUtils.isNumeric(possibleIdString)) {
      possibleIdString = RegExUtils.removeAll(possibleIdString, "\\D");
    }

    possibleId = NumberParseUtils.tryParseLong(possibleIdString);

    return possibleId;
  }
}