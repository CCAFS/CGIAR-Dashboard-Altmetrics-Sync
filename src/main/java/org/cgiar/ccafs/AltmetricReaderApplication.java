package org.cgiar.ccafs;

import org.cgiar.ccafs.domain.marlo.AltmetricsErrorLog;
import org.cgiar.ccafs.domain.marlo.Phase;
import org.cgiar.ccafs.domain.marlo.ReportSynthesisAltmetric;
import org.cgiar.ccafs.dto.ReportSynthesisAltmetricDTO;
import org.cgiar.ccafs.manager.AltmetricsErrorLogManager;
import org.cgiar.ccafs.manager.PhaseManager;
import org.cgiar.ccafs.manager.ReportSynthesisAltmetricManager;
import org.cgiar.ccafs.mapper.ReportSynthesisAltmetricMapper;
import org.cgiar.ccafs.service.AltmetricService;
import org.cgiar.ccafs.service.DOIService;
import org.cgiar.ccafs.util.NumberParseUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
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

  private static final Logger LOG = LoggerFactory.getLogger(AltmetricReaderApplication.class);
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static ReportSynthesisAltmetric altmetricResponseToReportSynthesisAltmetric(JsonElement altmetricResponse) {
    if (altmetricResponse.isJsonNull()) {
      return null;
    }

    ReportSynthesisAltmetric reportSynthesisAltmetric = new ReportSynthesisAltmetric();
    String detailUrl = null;
    JsonElement element = JsonNull.INSTANCE;
    JsonObject object = altmetricResponse.getAsJsonObject();
    JsonObject aux = null;

    // apparently the score is located as a parameter of an image URL, strange.
    aux = object.getAsJsonObject("images");
    reportSynthesisAltmetric.setAttentionScore(aux != null ? getScore(aux) : 0);

    element = object.get("authors");
    reportSynthesisAltmetric.setAuthor(element != null ? getAuthorsAsString(element) : StringUtils.EMPTY);

    reportSynthesisAltmetric
      .setBlogTotal(object.get("cited_by_feeds_count") != null ? object.get("cited_by_feeds_count").getAsInt() : 0);

    detailUrl = object.get("details_url") != null ? object.get("details_url").getAsString() : StringUtils.EMPTY;
    if (detailUrl.contains("details.php")) {
      detailUrl = AltmetricService.formatDetailUrlAltmetric(detailUrl);
    }

    reportSynthesisAltmetric.setDetailUrl(detailUrl);

    reportSynthesisAltmetric.setDoi(
      object.get("doi") != null ? DOIService.tryGetDoiName(object.get("doi").getAsString()) : StringUtils.EMPTY);
    reportSynthesisAltmetric.setFacebookTotal(
      object.get("cited_by_fbwalls_count") != null ? object.get("cited_by_fbwalls_count").getAsInt() : 0);
    reportSynthesisAltmetric.setImageUrl(aux != null ? getImageUrl(aux) : StringUtils.EMPTY);
    reportSynthesisAltmetric
      .setJournalTitle(object.get("journal") != null ? object.get("journal").getAsString() : StringUtils.EMPTY);
    reportSynthesisAltmetric.setLastUpdated(LocalDateTime.now().format(DATE_TIME_FORMATTER));

    aux = object.getAsJsonObject("readers");
    reportSynthesisAltmetric.setMendeleyTotal(aux.get("mendeley") != null ? aux.get("mendeley").getAsInt() : 0);

    reportSynthesisAltmetric
      .setNewsTotal(object.get("cited_by_msm_count") != null ? object.get("cited_by_msm_count").getAsInt() : 0);
    reportSynthesisAltmetric.setPolicyTotal(
      object.get("cited_by_policies_count") != null ? object.get("cited_by_policies_count").getAsInt() : 0);
    // FIXME THIS IS A TEMPORAL SOLUTION
    reportSynthesisAltmetric.setPublicationDate(object.get("published_on") != null
      ? DATE_FORMATTER.format(LocalDate.ofEpochDay(TimeUnit.SECONDS.toDays(object.get("published_on").getAsLong())))
      : null);
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
    String names = null;
    if (element.isJsonArray()) {
      array = element.getAsJsonArray();
      for (JsonElement e : array) {
        String author = StringUtils.stripToEmpty(e.getAsString());
        if (!author.isEmpty()) {
          sb.append(author).append(';');
        }
      }
    } else if (element.isJsonPrimitive()) {
      sb.append(element.getAsString());
    }

    names = sb.toString().trim();
    return StringUtils.isNotBlank(names) && names.charAt(names.length() - 1) == ';'
      ? names.substring(0, names.length() - 1) : names;
  }

  @SuppressWarnings("unused")
  private static String getAuthorsAsStringOld(JsonElement element) {
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

  private static String getImageUrl(JsonObject images) {
    String imageUrl = null;
    JsonElement element = images.get("small");

    if (element == null) {
      element = images.get("medium");
      if (element == null) {
        element = images.get("large");
      }
    }

    if (element != null && element.isJsonPrimitive()) {
      imageUrl = element.getAsString();
    }

    return imageUrl;
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
      score = NumberParseUtils.tryParseInt(string);
    }

    return score;
  }

  public static void main(String[] args) {
    SpringApplication.run(AltmetricReaderApplication.class, args);
  }

  @Autowired
  ReportSynthesisAltmetricManager reportSynthesisAltmetricManager;

  @Autowired
  PhaseManager phaseManager;

  @Autowired
  AltmetricsErrorLogManager altmetricsErrorLogManager;

  @Override
  public void run(String... args) throws Exception {
    LOG.info("EXECUTING : command line runner");

    this.updateReportSyntesisAltmetrics();
  }

  public void updateReportSyntesisAltmetrics() throws Exception {
    ReportSynthesisAltmetricMapper reportSynthesisAltmetricMapper =
      Mappers.getMapper(ReportSynthesisAltmetricMapper.class);

    LOG.info("1. Getting all altmetrics data from database...");
    List<ReportSynthesisAltmetricDTO> databaseAltmetrics = reportSynthesisAltmetricManager.findByCRPAcronym("fish")
      .stream().map(reportSynthesisAltmetricMapper::reportSynthesisAltmetricToReportSynthesisAltmetricDTO)
      .collect(Collectors.toList());
    LOG.info("Done! Found {} report(s).", databaseAltmetrics.size());

    Comparator<ReportSynthesisAltmetric> reportComparator = (a1, a2) -> a1.getId().compareTo(a2.getId());
    Optional<Phase> phase = Optional.empty();

    Set<ReportSynthesisAltmetricDTO> failedByDoiName = new HashSet<>();
    AltmetricsErrorLog altmetricsErrorLog = null;
    Set<AltmetricsErrorLog> failed = new HashSet<>();
    SortedSet<ReportSynthesisAltmetric> incomingAltmetricsById = new TreeSet<>(reportComparator);

    ReportSynthesisAltmetric altmetricResponseMapped = null;
    ReportSynthesisAltmetric aux = null;
    // ReportSynthesisAltmetricDTO reportSynthesisAltmetricDTO = null;

    String string = null;
    JsonElement altmetricResponse = null;
    long altmetricId = -1;

    // Gson gson = new Gson();
    // StringBuilder sb = new StringBuilder(4096);
    // Set<ReportSynthesisAltmetricDTO> incomingAltmetricsDTOById = new HashSet<>();

    LOG.info("2. Finding with the Altmetric API the info by DOI name ...");
    for (ReportSynthesisAltmetricDTO rsa : databaseAltmetrics) {
      string = StringUtils.stripToNull(rsa.getDoi());
      if (Objects.nonNull(string)) {
        string = DOIService.tryGetDoiName(string);
        try {
          altmetricResponse = AltmetricService.readDoiFromAltmetric(string);
        } catch (IOException e) {
          e.printStackTrace();
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
          // reportSynthesisAltmetricDTO = reportSynthesisAltmetricMapper
          // .reportSynthesisAltmetricToReportSynthesisAltmetricDTO(altmetricResponseMapped);
          incomingAltmetricsById.add(altmetricResponseMapped);
        }
      } else {
        failedByDoiName.add(rsa);
      }
    }
    LOG.info("Done!");

    if (!failedByDoiName.isEmpty()) {
      String logString = failedByDoiName.size() != 1
        ? "2.1. There were {} reports that could not be found by their DOI Name, retrying by their Altmetric id..."
        : "2.1. There was {} report that could not be found by its DOI Name, retrying by its Altmetric id...";
      LOG.info(logString, failedByDoiName.size());

      for (ReportSynthesisAltmetricDTO rsa : failedByDoiName) {
        string = StringUtils.stripToNull(rsa.getDetailUrl());
        if (string != null) {
          altmetricId = AltmetricService.tryGetAltmetricId(string);
          try {
            altmetricResponse = AltmetricService.readIdFromAltmetric(altmetricId);
          } catch (IOException e) {
            e.printStackTrace();
            // failedByDoiName.add(rsa);
            altmetricResponse = JsonNull.INSTANCE;
          }

          altmetricResponseMapped = altmetricResponseToReportSynthesisAltmetric(altmetricResponse);
          if (altmetricResponseMapped == null) {
            altmetricsErrorLog = new AltmetricsErrorLog();
            altmetricsErrorLog.setErrorDescription("Could not be found by DOI Name or Altmetric ID");

            // orElse should NEVER happen, data was taken directly from the database
            aux = reportSynthesisAltmetricManager.findById(rsa.getId()).orElse(null);
            altmetricsErrorLog.setReportSynthesisAltmetric(aux);

            altmetricsErrorLog.setUpdatedDate(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            failed.add(altmetricsErrorLog);
          } else {

            altmetricResponseMapped.setId(rsa.getId());
            phase = phaseManager.findById(rsa.getPhaseDTO().getId());
            // orElse should NEVER happen, data was taken directly from the database
            altmetricResponseMapped.setPhase(phase.orElse(null));
            // reportSynthesisAltmetricDTO = reportSynthesisAltmetricMapper
            // .reportSynthesisAltmetricToReportSynthesisAltmetricDTO(altmetricResponseMapped);
            incomingAltmetricsById.add(altmetricResponseMapped);
          }
        }
      }

      LOG.info("Done!");
      if (!failed.isEmpty()) {
        List<String> ids =
          failed.stream().map(a -> a.getReportSynthesisAltmetric().getId()).collect(Collectors.toList());
        LOG.info("There were {} report(s) which information could not be fetched: {}", failed.size(), ids);
      }
    }

    LOG.info("3. Updating {} database entries...", incomingAltmetricsById.size());
    for (ReportSynthesisAltmetric rsa : incomingAltmetricsById) {
      reportSynthesisAltmetricManager.update(rsa);
    }

    if (!failed.isEmpty()) {
      for (AltmetricsErrorLog errorLog : failed) {
        altmetricsErrorLogManager.save(errorLog);
      }
    }
    LOG.info("Done!");

    // for debugging purposes
    // incomingAltmetricsDTOById = incomingAltmetricsById.stream()
    // .map(reportSynthesisAltmetricMapper::reportSynthesisAltmetricToReportSynthesisAltmetricDTO)
    // .collect(Collectors.toSet());
    //
    // sb = sb.append("database information = \n").append(gson.toJson(databaseAltmetrics)).append('\n')
    // .append("--------------------------------------------------------------------------\n").append("succeded = \n")
    // .append(gson.toJson(incomingAltmetricsDTOById)).append('\n')
    // .append("--------------------------------------------------------------------------\n")
    // .append("failed by DOI = \n").append(gson.toJson(failedByDoiName)).append('\n')
    // .append("--------------------------------------------------------------------------\n")
    // .append("failed by both Altmetric Id and DOI = \n").append(gson.toJson(failed));
    //
    // Files.write(Paths.get("D:\\misc\\txts\\altmetric-prod-update\\dumpAltmetricDoiReportProd.txt"),
    // sb.toString().getBytes(StandardCharsets.UTF_8));
  }
}
