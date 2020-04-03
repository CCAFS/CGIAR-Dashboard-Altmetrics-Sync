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

import org.cgiar.ccafs.domain.marlo.DeliverableMetadataElement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public interface DeliverableMetadataElementDAO extends JpaRepository<DeliverableMetadataElement, String> {

  @Query("SELECT DISTINCT TRIM(dme.elementValue) FROM DeliverableMetadataElement dme LEFT JOIN dme.metadataElement me "
    + "WHERE me.id = 36 AND TRIM(COALESCE(dme.elementValue, '')) != '' ORDER BY dme.elementValue")
  public List<String> findAllDois();
}
