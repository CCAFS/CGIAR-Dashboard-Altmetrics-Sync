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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public interface ReportSynthesisAltmetricDAO extends JpaRepository<ReportSynthesisAltmetric, String> {

  @Query(value = "SELECT rsa.* FROM report_synthesis_altmetrics rsa INNER JOIN phases p ON rsa.id_phase = p.id "
    + "INNER JOIN global_units g ON p.global_unit_id = g.id WHERE g.acronym LIKE :acronym", nativeQuery = true)
  public List<ReportSynthesisAltmetric> findByCRPAcronym(@Param("acronym") String crpAcronym);

}
