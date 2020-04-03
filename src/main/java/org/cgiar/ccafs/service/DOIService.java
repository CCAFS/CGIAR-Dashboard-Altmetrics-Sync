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
import org.cgiar.ccafs.util.DOIHttpResponseCode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class DOIService {

  /**
   * DOI service URL. It returns a JSON as described in the DOI
   * <a href = "https://www.doi.org/factsheets/DOIProxy.html#rest-api">factsheet</a>
   */
  public final static String DOI_SERVICE = "https://doi.org/api/handles/%s";

  public final static String DOI_SERVICE_RESPONSE_CODE = "responseCode";

  private static JsonElement getDoiElement(final String doi) throws IOException {
    URL shortDoiServiceURL = new URL(String.format(DOI_SERVICE, doi));
    HttpURLConnection connection = (HttpURLConnection) shortDoiServiceURL.openConnection();
    JsonElement element = JsonNull.INSTANCE;

    if (connection.getResponseCode() < 299) {
      try (InputStreamReader reader = new InputStreamReader(shortDoiServiceURL.openStream())) {
        element = JsonParser.parseReader(reader);
      } catch (FileNotFoundException fnfe) {
        // nothing
        fnfe.printStackTrace();
      }
    } else {
      throw new InvalidDOIException(doi);
    }

    return element;
  }

  public static String getDoiFromShortDoi(final String shortDoi) throws IOException {
    JsonElement element = getDoiElement(shortDoi);
    String doi = null;
    if (element.isJsonObject()) {
      JsonObject object = element.getAsJsonObject();
      DOIHttpResponseCode responseCode =
        DOIHttpResponseCode.getByErrorCode(object.get(DOI_SERVICE_RESPONSE_CODE).getAsInt());
      switch (responseCode) {
        case SUCCESS:
          // TODO we are not sure if it ALWAYS is an array, so we need to check if it is a JsonArray first
          JsonArray array = object.get("values").getAsJsonArray();
          object = array.get(1).getAsJsonObject().get("data").getAsJsonObject();
          doi = object.get("value").getAsString();
          break;

        case VALUES_NOT_FOUND:
          // fall-through
        case HANDLE_NOT_FOUND:
          // fall-through
        case ERROR:
          doi = StringUtils.EMPTY;
          break;
      }
    }

    return doi;
  }

}
