package org.cgiar.ccafs;

import org.cgiar.ccafs.dao.ReportSynthesisAltmetricDAO;
import org.cgiar.ccafs.domain.marlo.ReportSynthesisAltmetric;
import org.cgiar.ccafs.manager.ReportSynthesisAltmetricManager;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AltmetricReaderApplicationTests {

  @Autowired
  ReportSynthesisAltmetricDAO reportSynthesisAltmetricDAO;

  @Autowired
  ReportSynthesisAltmetricManager reportSynthesisAltmetricManager;

  @Test
  @DisplayName("reportSynthesisAltmetricDAOInjection")
  void testRsaDao() {
    assertNotNull(reportSynthesisAltmetricDAO, "why is this even null?");
  }

  @Test
  @DisplayName("reportSynthesisAltmetricDAOFindAll")
  void testRsaDaoFindAll() {
    List<ReportSynthesisAltmetric> elements = reportSynthesisAltmetricDAO.findAll();
    assertNotNull(elements, "that is strange...");
    elements.forEach(System.out::println);
  }

  @Test
  @DisplayName("reportSynthesisAltmetricManagerInjection")
  void testRsaManager() {
    assertNotNull(reportSynthesisAltmetricManager, "why is this even null? x2");
  }

  @Test
  @DisplayName("reportSynthesisAltmetricManagerFindAll")
  void testRsaManagerFindAll() {
    List<ReportSynthesisAltmetric> elements = reportSynthesisAltmetricManager.findAll();
    assertNotNull(elements, "that is strange... x2");
    elements.forEach(System.out::println);
  }

  // TODO tests for every method

}
