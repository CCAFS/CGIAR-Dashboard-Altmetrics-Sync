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

package org.cgiar.ccafs.dao;

import org.cgiar.ccafs.domain.marlo.ReportSynthesisAltmetric;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@SpringBootTest
class ReportSynthesisAltmetricDAOTests {

  public static final Logger LOG = LoggerFactory.getLogger(ReportSynthesisAltmetricDAOTests.class);

  @Autowired
  ReportSynthesisAltmetricDAO reportSynthesisAltmetricDAO;

  @Test
  @DisplayName("findByCRPAcronym")
  void findCrpTest() {
    List<ReportSynthesisAltmetric> result = reportSynthesisAltmetricDAO.findByCRPAcronym("ccafs");
    assertNotNull(result, "findByCRPAcronym returned null...");
    assertNotEquals(result.size(), 0);
    LOG.info(result.toString());
  }

  @Test
  @DisplayName("reportSynthesisAltmetricDAOInjection")
  void testRsaDao() {
    assertNotNull(reportSynthesisAltmetricDAO, "reportSynthesisAltmetricDAO is null");
  }

}
