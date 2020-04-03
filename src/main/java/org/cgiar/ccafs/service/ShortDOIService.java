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

import org.cgiar.ccafs.exception.InvalidDOIException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**************
 * <p>
 * Uses the shortDOI service to shorten a DOI. The shortened DOI acts exactly like its long counterpart. If the DOI is
 * invalid, it throws an {@link org.cgiar.ccafs.exception.InvalidDOIException InvalidDOIException}
 * </p>
 * <p>
 * Example:
 * <blockquote>
 * <ul>
 * <li>Input: {@code 10.1007/978-3-319-29794-1_9}; Output: {@code 10/dqnq}
 * <li>Input: {@code 10.1007/-1_9}; throws {@code InvalidDOIException}
 * </blockquote>
 * </p>
 * 
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ShortDOIService {

  /**
   * ShortDOI service URL. It returns a JSON with the DOI entered, its ShortDOI equivalent and a boolean indicating if
   * the ShortDOI was created or if it already existed.
   */
  public final static String SHORT_DOI_SERVICE = "http://shortdoi.org/%s?format=json";

  public final static String SHORT_DOI = "ShortDOI";
  public final static String IS_NEW = "IsNew";

  /**
   * Shortens a DOI using shortDOI service.
   * 
   * @param doi the DOI to shorten
   * @return a shortDOI identifier
   * @throws IOException
   */
  public static String getShortDoi(final String doi) throws IOException {
    JsonElement element = getShortDoiElement(doi);

    return element.isJsonObject() ? ((JsonObject) element).get(SHORT_DOI).getAsString() : null;
  }

  private static JsonElement getShortDoiElement(final String doi) throws IOException {
    URL shortDoiServiceURL = new URL(String.format(SHORT_DOI_SERVICE, doi));
    HttpURLConnection connection = (HttpURLConnection) shortDoiServiceURL.openConnection();
    JsonElement element = JsonNull.INSTANCE;

    if (connection.getResponseCode() < 299) {
      try (InputStreamReader reader = new InputStreamReader(shortDoiServiceURL.openStream())) {
        element = JsonParser.parseReader(reader);
      } catch (FileNotFoundException fnfe) {
        // nothing
      }
    } else {
      throw new InvalidDOIException(doi);
    }

    return element;
  }

  public static Boolean isNewShortDoi(final String doi) throws IOException {
    JsonElement element = getShortDoiElement(doi);

    return element.isJsonObject() ? ((JsonObject) element).get(IS_NEW).getAsBoolean() : null;
  }

}
