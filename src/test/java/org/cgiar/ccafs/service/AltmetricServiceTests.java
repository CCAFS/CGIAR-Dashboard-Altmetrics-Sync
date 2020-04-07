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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@SpringBootTest
class AltmetricServiceTests {

  public static final Logger LOG = LoggerFactory.getLogger(AltmetricServiceTests.class);

  @Test
  @DisplayName("readDoiRiceTest")
  void doiReadTest() {
    String[] originalUrls;
    JsonElement element = JsonNull.INSTANCE;
    String doi = null;
    List<String> doisNotFound = new ArrayList<>();

    try {
      originalUrls = new String(Files.readAllBytes(Paths.get("D:\\misc\\txts\\doisRice.txt"))).split("\r\n");
      for (String url : originalUrls) {
        doi = DOIService.tryGetDoiName(url);
        element = AltmetricService.readDoiFromAltmetric(doi);
        if (element == JsonNull.INSTANCE) {
          doisNotFound.add(doi);
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    LOG.info("There were report(s) not found with doi(s) {}", doisNotFound.toString());
  }

  @Test
  @DisplayName("urlAfterFormatTest")
  void formatTest() {
    String[] originalUrls;
    // StringBuilder sb = new StringBuilder();
    try {
      originalUrls = new String(Files.readAllBytes(Paths.get("D:\\misc\\originalUrls.txt"))).split("\r\n");
      String[] urlsAfterRedirection = new String[originalUrls.length];
      String url;
      for (int i = 0; i < originalUrls.length; i++) {
        url = StringUtils.stripToEmpty(originalUrls[i]);
        if (!url.isEmpty()) {
          url = AltmetricService.formatDetailUrlAltmetric(url);
        }

        urlsAfterRedirection[i] = url;
        // sb.append(url).append("\r\n");
      }

      LOG.info("original urls: {}", Arrays.toString(originalUrls));
      LOG.info("urlsAfterRedirection: {}", Arrays.toString(urlsAfterRedirection));
      // Files.write(Paths.get("D:\\misc\\newUrls.txt"), sb.toString().getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("urlAfterRedirectionTest")
  void redirectionTest() {
    try {
      String[] originalUrls = new String(Files.readAllBytes(Paths.get("D:\\misc\\originalUrls.txt"))).split("\r\n");
      String[] urlsAfterRedirection = new String[originalUrls.length];
      String url;
      for (int i = 0; i < originalUrls.length; i++) {
        url = StringUtils.stripToEmpty(originalUrls[i]);
        if (!url.isEmpty() && url.contains("details.php")) {
          try {
            url = AltmetricService.followRedirectDetailUrlAltmetric(url);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        urlsAfterRedirection[i] = url;
      }

      LOG.info("original urls: {}", Arrays.toString(originalUrls));
      LOG.info("urlsAfterRedirection: {}", Arrays.toString(urlsAfterRedirection));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
