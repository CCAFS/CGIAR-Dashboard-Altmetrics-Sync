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

package org.cgiar.ccafs.main;

import org.cgiar.ccafs.domain.marlo.ReportSynthesisAltmetric;
import org.cgiar.ccafs.exception.InvalidDOIException;
import org.cgiar.ccafs.service.DOIService;
import org.cgiar.ccafs.util.DatabaseOperations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class Main {

  public final static String ALTMETRICS_API_URL = "https://api.altmetric.com/v1/";
  public final static String ALTMETRICS_API_DOI_URL = ALTMETRICS_API_URL + "doi/";
  public final static String ALTMETRICS_API_ID_URL = ALTMETRICS_API_URL + "id/";

  /**
   * Pattern defined according to the ShortDOI <a href = "http://shortdoi.org/">webpage</a>.
   */
  public final static Pattern SHORT_DOI_PATTERN = Pattern.compile("^\\d{2}\\/\\w{3,5}$", Pattern.MULTILINE);

  private static ReportSynthesisAltmetric altmetricResponseToReportSynthesisAltmetric(JsonElement altmetricResponse) {
    if (altmetricResponse.isJsonNull()) {
      return null;
    }

    ReportSynthesisAltmetric reportSynthesisAltmetric = new ReportSynthesisAltmetric();
    JsonElement element = JsonNull.INSTANCE;
    JsonObject object = altmetricResponse.getAsJsonObject();
    JsonObject aux = null;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // apparently the score is located as a parameter of an image URL, strange.
    aux = object.getAsJsonObject("images");
    reportSynthesisAltmetric.setAttentionScore(aux != null ? getScore(aux) : 0);

    element = object.get("authors");
    reportSynthesisAltmetric.setAuthor(element != null ? getAuthorsAsString(element) : StringUtils.EMPTY);

    reportSynthesisAltmetric
      .setBlogTotal(object.get("cited_by_feeds_count") != null ? object.get("cited_by_feeds_count").getAsInt() : 0);
    reportSynthesisAltmetric
      .setDetailUrl(object.get("details_url") != null ? object.get("details_url").getAsString() : StringUtils.EMPTY);
    reportSynthesisAltmetric
      .setDoi(object.get("doi") != null ? tryGetDoiIdentifier(object.get("doi").getAsString()) : StringUtils.EMPTY);
    reportSynthesisAltmetric.setFacebookTotal(
      object.get("cited_by_fbwalls_count") != null ? object.get("cited_by_fbwalls_count").getAsInt() : 0);
    reportSynthesisAltmetric
      .setJournalTitle(object.get("journal") != null ? object.get("journal").getAsString() : StringUtils.EMPTY);

    aux = object.getAsJsonObject("readers");
    reportSynthesisAltmetric.setMendeleyTotal(aux.get("mendeley") != null ? aux.get("mendeley").getAsInt() : 0);

    reportSynthesisAltmetric
      .setNewsTotal(object.get("cited_by_msm_count") != null ? object.get("cited_by_msm_count").getAsInt() : 0);
    reportSynthesisAltmetric.setPolicyTotal(
      object.get("cited_by_policies_count") != null ? object.get("cited_by_policies_count").getAsInt() : 0);
    /*
     * FIXME THIS IS A TEMPORAL SOLUTION
     */
    reportSynthesisAltmetric.setPublicationDate(object.get("published_on") != null
      ? dtf.format(LocalDate.ofEpochDay(TimeUnit.SECONDS.toDays(object.get("published_on").getAsLong()))) : null);
    reportSynthesisAltmetric
      .setTitle(object.get("title") != null ? object.get("title").getAsString() : StringUtils.EMPTY);
    reportSynthesisAltmetric.setTwitterTotal(
      object.get("cited_by_tweeters_count") != null ? object.get("cited_by_tweeters_count").getAsInt() : 0);
    reportSynthesisAltmetric.setWikipediaTotal(
      object.get("cited_by_wikipedia_count") != null ? object.get("cited_by_wikipedia_count").getAsInt() : 0);

    return reportSynthesisAltmetric;
  }

  private static void auxGetAuthorAsString(String[] split, StringBuilder sb) {
    int length = split.length;
    String string = null;
    if (length > 1) {
      sb.append(split[length - 1]).append(',').append(' ');
      for (int i = 0; i < length - 1; i++) {
        string = StringUtils.stripToEmpty(split[i]);
        if (!string.isEmpty()) {
          sb.append(string.charAt(0)).append('.');
          if (i != length - 2) {
            sb.append(' ');
          }
        }
      }
    } else {
      sb.append(split);
    }
    sb.append(';').append(' ');
  }

  private static String getAuthorsAsString(JsonElement element) {
    JsonArray array = null;
    StringBuilder sb = new StringBuilder();
    String[] split = null;

    if (element.isJsonArray()) {
      array = element.getAsJsonArray();
      for (JsonElement e : array) {
        String author = StringUtils.stripToEmpty(e.getAsString());
        if (!author.isEmpty()) {
          split = author.split("\\s");
          auxGetAuthorAsString(split, sb);
        }
      }
    } else if (element.isJsonPrimitive()) {
      split = element.getAsString().split("\\s");
      auxGetAuthorAsString(split, sb);
    }

    return sb.toString();
  }

  private static int getScore(JsonObject images) {
    int score = -1;
    String string = null;
    JsonElement element = images.get("small");

    if (element == null) {
      element = images.get("medium");
      if (element == null) {
        element = images.get("large");
      }
    }

    if (element != null && element.isJsonPrimitive()) {
      string = element.getAsString();
      string = StringUtils.stripToEmpty(StringUtils.substringBefore(StringUtils.substringAfter(string, "score="), "&"));
      score = tryParseInt(string);
    }

    return score;
  }

  public static void main(String[] args) throws IOException, UnsupportedEncodingException {
    JsonElement element = readIdFromAltmetric(45504266);
    System.out.printf("element = %s\n", element);
    ReportSynthesisAltmetric altmetric = altmetricResponseToReportSynthesisAltmetric(element);
    System.out.printf("reportSynthesis = %s\n", altmetric);
  }

  public static JsonElement readDoiFromAltmetric(final String doi) throws IOException, UnsupportedEncodingException {
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

  /**
   * Tries to get a DOI name from a given string.
   * <p>
   * A DOI name is defined as the path component of an URI, e.g. if your input string is
   * {@code https://www.doi.org/10.1007/978-3-319-29794-1_9}, the result will be {@code 10.1007/978-3-319-29794-1_9}.
   * </p>
   * <p>
   * This method currently recognizes and tries to get the DOI from strings like:
   * <blockquote>
   * <ul>
   * <li>https://www.doi.org/10.1007/978-3-319-29794-1_9
   * <li>http://dx.doi.org/10.1007/978-3-319-29794-1_9
   * <li>http://shortdoi.org/10.1007/978-3-319-29794-1_9
   * <li>https%3A%2F%2Fwww.doi.org%2F10.1007%2F978-3-319-29794-1_9
   * <li>http%3A%2F%2Fdx.doi.org%2F10.1007%2F978-3-319-29794-1_9
   * <li>http%3A%2F%2Fshortdoi.org%2F10%2Fdqnq
   * <li>10/dqnq
   * <li>/10/dqnq
   * <li>10.1007/978-3-319-29794-1_9
   * <li>/10.1007/978-3-319-29794-1_9
   * <li>doi 10.1007/978-3-319-29794-1_9
   * <li>doi:10.1007/978-3-319-29794-1_9
   * </ul>
   * </p>
   * </blockquote>
   * 
   * @param possibleDoi a String containing the possible DOI name
   * @return the DOI name found, {@link org.apache.commons.lang3.StringUtils.EMPTY empty} if could not be found or is
   *         invalid
   */
  private static String tryGetDoiIdentifier(final String possibleDoi) {
    if (StringUtils.isBlank(possibleDoi)) {
      return StringUtils.EMPTY;
    }

    String doi = possibleDoi;
    if (StringUtils.contains(doi, "%")) {
      // possible encoded url...
      try {
        doi = URLDecoder.decode(doi, StandardCharsets.UTF_8.name());
      } catch (UnsupportedEncodingException e) {
        // ...maybe not, nothing we can do.
        e.printStackTrace();
      }
    }

    if (StringUtils.contains(doi, "doi.org/")) {
      doi = StringUtils.substringAfter(doi, "doi.org/");
    } else if (StringUtils.startsWithIgnoreCase(doi, "doi")) {
      doi = StringUtils.substringAfter(doi, "doi");
      if (StringUtils.startsWith(possibleDoi, ":")) {
        doi = doi.substring(1);
      }
    } else if (StringUtils.isNotBlank(doi) && Character.isDigit(doi.charAt(0))) {
      // continue
    } else {
      doi = StringUtils.EMPTY;
    }

    if (SHORT_DOI_PATTERN.matcher(doi).matches()) {
      try {
        // possible shortDOI...
        doi = DOIService.getDoiFromShortDoi(doi);
      } catch (IOException | InvalidDOIException e) {
        // ... maybe not
        doi = StringUtils.EMPTY;
      }
    }

    // possible leading slash (/)
    return StringUtils.isNotBlank(doi) && !Character.isDigit(doi.charAt(0)) ? doi.substring(1) : doi;
  }

  public static int tryParseInt(String value) {
    int result = -1;
    try {
      result = Integer.parseInt(value);
    } catch (NumberFormatException nfe) {
      // nothing we can do
    }

    return result;
  }

  public static void updateReportSyntesisAltmetrics() {
    List<ReportSynthesisAltmetric> reportList = DatabaseOperations.getReportSynthesisAltmetricList(false);
    JsonElement element = JsonNull.INSTANCE;
    ReportSynthesisAltmetric incoming = null;
    String doi = null;
    for (ReportSynthesisAltmetric reportSynthesisAltmetric : reportList) {
      doi = tryGetDoiIdentifier(reportSynthesisAltmetric.getDoi());
      try {
        element = readDoiFromAltmetric(doi);
        incoming = altmetricResponseToReportSynthesisAltmetric(element);
      } catch (IOException e) {

      }
    }
  }
}
