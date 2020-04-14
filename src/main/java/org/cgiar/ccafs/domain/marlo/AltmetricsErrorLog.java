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

package org.cgiar.ccafs.domain.marlo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.springframework.transaction.annotation.Transactional;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Entity
@Table(name = "altmetrics_error_log")
@NamedQuery(name = "AltmetricsErrorLog.findAll", query = "SELECT a FROM AltmetricsErrorLog a")
@Transactional
public class AltmetricsErrorLog extends MarloBaseEntity<Long> implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -105844745183729887L;

  @ManyToOne
  @JoinColumn(name = "report_synthesis_altmetrics_id")
  ReportSynthesisAltmetric reportSynthesisAltmetric;

  // @Temporal(TemporalType.DATE)
  @Column(name = "updated_date")
  String updatedDate;

  @Lob
  String errorDescription;

  public AltmetricsErrorLog() {
  }

  public String getErrorDescription() {
    return errorDescription;
  }

  public ReportSynthesisAltmetric getReportSynthesisAltmetric() {
    return reportSynthesisAltmetric;
  }

  public String getUpdatedDate() {
    return updatedDate;
  }


  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }

  public void setReportSynthesisAltmetric(ReportSynthesisAltmetric reportSynthesisAltmetric) {
    this.reportSynthesisAltmetric = reportSynthesisAltmetric;
  }

  public void setUpdatedDate(String updatedDate) {
    this.updatedDate = updatedDate;
  }

}
