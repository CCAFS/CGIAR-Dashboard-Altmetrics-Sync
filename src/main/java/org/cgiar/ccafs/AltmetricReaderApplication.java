package org.cgiar.ccafs;

import org.cgiar.ccafs.domain.marlo.Phase;
import org.cgiar.ccafs.domain.marlo.ReportSynthesisAltmetric;
import org.cgiar.ccafs.dto.ReportSynthesisAltmetricDTO;
import org.cgiar.ccafs.exception.InvalidDOIException;
import org.cgiar.ccafs.manager.PhaseManager;
import org.cgiar.ccafs.manager.ReportSynthesisAltmetricManager;
import org.cgiar.ccafs.mapper.ReportSynthesisAltmetricMapper;
import org.cgiar.ccafs.service.DOIService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AltmetricReaderApplication implements CommandLineRunner {

  private static Logger LOG = LoggerFactory.getLogger(AltmetricReaderApplication.class);

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
      .setDoi(object.get("doi") != null ? tryGetDoiName(object.get("doi").getAsString()) : StringUtils.EMPTY);
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
    // FIXME THIS IS A TEMPORAL SOLUTION
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
      sb.append(';').append(' ');
    } else {
      sb.append(split);
    }
  }

  private static String getAuthorsAsString(JsonElement element) {
    JsonArray array = null;
    StringBuilder sb = new StringBuilder();
    String[] split = null;
    String name = null;

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

    name = sb.toString().trim();

    return StringUtils.isNotBlank(name) && name.charAt(name.length() - 1) == ';' ? name.substring(0, name.length() - 1)
      : name;
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

  public static void main(String[] args) {
    SpringApplication.run(AltmetricReaderApplication.class, args);
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

  private static Long tryGetAltmetricId(final String altmetricUrl) {
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

    possibleId = tryParseLong(possibleIdString);

    return possibleId;
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
  private static String tryGetDoiName(final String possibleDoi) {
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
      if (StringUtils.startsWith(doi, ":")) {
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

  public static long tryParseLong(String value) {
    long result = -1;
    try {
      result = Long.parseLong(value);
    } catch (NumberFormatException nfe) {
      // nothing we can do
    }

    return result;
  }

  @Autowired
  ReportSynthesisAltmetricManager reportSynthesisAltmetricManager;

  @Autowired
  PhaseManager phaseManager;

  @Override
  public void run(String... args) throws Exception {
    LOG.info("EXECUTING : command line runner");

    ReportSynthesisAltmetricMapper reportSynthesisAltmetricMapper =
      Mappers.getMapper(ReportSynthesisAltmetricMapper.class);

    LOG.info("1. Getting all altmetrics data from database...");
    List<ReportSynthesisAltmetricDTO> databaseAltmetrics = reportSynthesisAltmetricManager.findAll().stream()
      .map(reportSynthesisAltmetricMapper::reportSynthesisAltmetricToReportSynthesisAltmetricDTO)
      .collect(Collectors.toList());
    LOG.info("Done! Found {} report(s).", databaseAltmetrics.size());

    Comparator<ReportSynthesisAltmetricDTO> reportComparator = (a1, a2) -> a1.getId().compareTo(a2.getId());
    Optional<Phase> phase = Optional.empty();

    Set<ReportSynthesisAltmetricDTO> failedByDoiName = new HashSet<>();
    Set<ReportSynthesisAltmetricDTO> failed = new HashSet<>();
    SortedSet<ReportSynthesisAltmetricDTO> incomingAltmetricsById = new TreeSet<>(reportComparator);

    ReportSynthesisAltmetric altmetricResponseMapped = null;
    ReportSynthesisAltmetricDTO reportSynthesisAltmetricDTO = null;

    String string = null;
    JsonElement altmetricResponse = null;
    long altmetricId = -1;

    Gson gson = new Gson();
    StringBuilder sb = new StringBuilder(4096);

    LOG.info("2. Finding with the altmetric API the info by DOI name ...");
    for (ReportSynthesisAltmetricDTO rsa : databaseAltmetrics) {
      string = StringUtils.stripToNull(rsa.getDoi());
      if (Objects.nonNull(string)) {
        string = tryGetDoiName(string);
        try {
          altmetricResponse = readDoiFromAltmetric(string);
        } catch (IOException e) {
          e.printStackTrace();
          // failedByDoiName.add(rsa);
          altmetricResponse = JsonNull.INSTANCE;
        }

        altmetricResponseMapped = altmetricResponseToReportSynthesisAltmetric(altmetricResponse);
        if (altmetricResponseMapped == null) {
          failedByDoiName.add(rsa);
        } else {
          altmetricResponseMapped.setId(rsa.getId());
          phase = phaseManager.findById(rsa.getPhaseDTO().getId());
          // orElse should NEVER happen, data was taken directly from the database
          altmetricResponseMapped.setPhase(phase.orElse(null));
          reportSynthesisAltmetricDTO = reportSynthesisAltmetricMapper
            .reportSynthesisAltmetricToReportSynthesisAltmetricDTO(altmetricResponseMapped);
          incomingAltmetricsById.add(reportSynthesisAltmetricDTO);
        }
      } else {
        failedByDoiName.add(rsa);
      }
    }
    LOG.info("Done!");

    if (!failedByDoiName.isEmpty()) {
      LOG.info("3. There were {} reports that could not be found by its DOI Name, retrying by their altmetric id...",
        failedByDoiName.size());

      for (ReportSynthesisAltmetricDTO rsa : failedByDoiName) {
        string = StringUtils.stripToNull(rsa.getDetailUrl());
        if (Objects.nonNull(string)) {
          altmetricId = tryGetAltmetricId(string);
          try {
            altmetricResponse = readIdFromAltmetric(altmetricId);
          } catch (IOException e) {
            e.printStackTrace();
            // failedByDoiName.add(rsa);
            altmetricResponse = JsonNull.INSTANCE;
          }

          altmetricResponseMapped = altmetricResponseToReportSynthesisAltmetric(altmetricResponse);
          if (altmetricResponseMapped == null) {
            failed.add(rsa);
          } else {
            altmetricResponseMapped.setId(rsa.getId());
            phase = phaseManager.findById(rsa.getPhaseDTO().getId());
            // orElse should NEVER happen, data was taken directly from the database
            altmetricResponseMapped.setPhase(phase.orElse(null));
            reportSynthesisAltmetricDTO = reportSynthesisAltmetricMapper
              .reportSynthesisAltmetricToReportSynthesisAltmetricDTO(altmetricResponseMapped);
            incomingAltmetricsById.add(reportSynthesisAltmetricDTO);
          }
        }
      }

      LOG.info("Done!");
      if (!failed.isEmpty()) {
        List<String> ids = failed.stream().map(a -> a.getId()).collect(Collectors.toList());
        LOG.info("There were {} reports which information could not be updated: {}", failed.size(), ids);
      }
    }

    sb = sb.append("database information = \n").append(gson.toJson(databaseAltmetrics)).append('\n')
      .append("--------------------------------------------------------------------------\n").append("succeded = \n")
      .append(gson.toJson(incomingAltmetricsById)).append('\n')
      .append("--------------------------------------------------------------------------\n")
      .append("failed by DOI = \n").append(gson.toJson(failedByDoiName)).append('\n')
      .append("--------------------------------------------------------------------------\n")
      .append("failed by both Altmetric Id and DOI = \n").append(gson.toJson(failed));

    Files.write(Paths.get("D:\\misc\\dumpAltmetricDoiReport.txt"), sb.toString().getBytes(StandardCharsets.UTF_8));
  }

  /*
   * WIP
   */
  // public static void updateReportSyntesisAltmetrics() {
  // List<ReportSynthesisAltmetric> reportList = reportSynthesisAltmetricManager.findAll();
  // JsonElement element = JsonNull.INSTANCE;
  // ReportSynthesisAltmetric incoming = null;
  // String doi = null;
  // for (ReportSynthesisAltmetric reportSynthesisAltmetric : reportList) {
  // doi = tryGetDoiName(reportSynthesisAltmetric.getDoi());
  // try {
  // element = readDoiFromAltmetric(doi);
  // incoming = altmetricResponseToReportSynthesisAltmetric(element);
  // } catch (IOException e) {
  //
  // }
  // }
}