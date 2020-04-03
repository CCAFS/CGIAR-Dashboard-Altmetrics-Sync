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

package org.cgiar.ccafs.dto;

import java.util.Date;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class PhaseDTO {

  @Expose
  private String id;

  @Expose
  private String description;

  @Expose
  private Boolean editable;

  @Expose
  private Date endDate;

  @Expose
  private java.math.BigInteger globalUnitId;

  @Expose
  private String name;

  @Expose
  private Date startDate;

  @Expose
  private Boolean upkeep;

  @Expose
  private Boolean visible;

  @Expose
  private Integer year;

  @Expose
  private Long nextPhaseId;

  public PhaseDTO() {
  }

  public String getDescription() {
    return description;
  }

  public Boolean getEditable() {
    return editable;
  }

  public Date getEndDate() {
    return endDate;
  }

  public java.math.BigInteger getGlobalUnitId() {
    return globalUnitId;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Long getNextPhaseId() {
    return nextPhaseId;
  }

  public Date getStartDate() {
    return startDate;
  }

  public Boolean getUpkeep() {
    return upkeep;
  }

  public Boolean getVisible() {
    return visible;
  }

  public Integer getYear() {
    return year;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setEditable(Boolean editable) {
    this.editable = editable;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public void setGlobalUnitId(java.math.BigInteger globalUnitId) {
    this.globalUnitId = globalUnitId;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNextPhaseId(Long nextPhaseId) {
    this.nextPhaseId = nextPhaseId;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public void setUpkeep(Boolean upkeep) {
    this.upkeep = upkeep;
  }

  public void setVisible(Boolean visible) {
    this.visible = visible;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  @Override
  public String toString() {
    return new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create().toJson(this);
  }
}
