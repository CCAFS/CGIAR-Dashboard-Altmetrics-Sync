/*****************************************************************
 * a * This file is part of Managing Agricultural Research for Learning &
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

package org.cgiar.ccafs.util;

import org.cgiar.ccafs.domain.marlo.ReportSynthesisAltmetric;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DatabaseOperations {

  private final static EntityManagerFactory entityManagerFactory;
  private final static EntityManager entityManager;
  static {
    entityManagerFactory = Persistence.createEntityManagerFactory("AltmetricsReader");
    entityManager = entityManagerFactory.createEntityManager();
  }

  private static void closeManagers() {
    entityManager.close();
    entityManagerFactory.close();
  }

  public static void ensureClose() {
    entityManager.close();
    entityManagerFactory.close();
  }

  public static List<String> getDois(boolean closeConnection) {
    List<String> doisDeliverables =
      entityManager.createQuery("SELECT DISTINCT TRIM(dme.elementValue) FROM DeliverableMetadataElement dme "
        + "LEFT JOIN dme.metadataElement me WHERE me.id = 36 AND TRIM(COALESCE(dme.elementValue, '')) != '' "
        + "ORDER BY dme.elementValue", String.class).getResultList();

    if (closeConnection) {
      closeManagers();
    }

    return doisDeliverables;
  }

  public static List<ReportSynthesisAltmetric> getReportSynthesisAltmetricList(boolean closeConnection) {
    List<ReportSynthesisAltmetric> listRsa = entityManager
      .createQuery("SELECT rsa FROM ReportSynthesisAltmetric rsa", ReportSynthesisAltmetric.class).getResultList();

    if (closeConnection) {
      closeManagers();
    }

    return listRsa;
  }

  public static ReportSynthesisAltmetric updateReportSynthesisAltmetric(ReportSynthesisAltmetric updated,
    boolean closeConnection) {
    ReportSynthesisAltmetric merged = entityManager.merge(updated);

    if (closeConnection) {
      closeManagers();
    }

    return merged;
  }

}
