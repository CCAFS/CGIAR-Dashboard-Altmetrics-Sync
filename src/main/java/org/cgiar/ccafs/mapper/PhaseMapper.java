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

package org.cgiar.ccafs.mapper;

import org.cgiar.ccafs.domain.marlo.Deliverable;
import org.cgiar.ccafs.domain.marlo.DeliverableMetadataElement;
import org.cgiar.ccafs.domain.marlo.Phase;
import org.cgiar.ccafs.domain.marlo.ReportSynthesisAltmetric;
import org.cgiar.ccafs.dto.PhaseDTO;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Mapper
public interface PhaseMapper {

  public default List<String> deliverableMetadataElementToDeliverableMetadataElementIds(
    List<DeliverableMetadataElement> deliverableMetadataElements) {
    return deliverableMetadataElements.stream().map(d -> d.getId()).collect(Collectors.toList());
  }

  public default List<String> deliverablesToDeliverablesIds(List<Deliverable> deliverables) {
    return deliverables.stream().map(d -> d.getId()).collect(Collectors.toList());
  }

  public default List<String> phasesToPhaseIdList(List<Phase> phases) {
    return phases.stream().map(p -> p.getId()).collect(Collectors.toList());
  }

  @Mappings({@Mapping(source = "phase.id", target = "nextPhaseId")})
  public abstract PhaseDTO phaseToPhaseDTO(Phase phase);

  public default List<String> reportSynthesisAltmetricListToReportSynthesisAltmetricIdList(
    List<ReportSynthesisAltmetric> reportSynthesisAltmetrics) {
    return reportSynthesisAltmetrics.stream().map(a -> a.getId()).collect(Collectors.toList());
  }
}
