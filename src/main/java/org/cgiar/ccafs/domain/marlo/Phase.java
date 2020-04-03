package org.cgiar.ccafs.domain.marlo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.transaction.annotation.Transactional;


/**
 * The persistent class for the phases database table.
 */
@Entity
@Table(name = "phases")
@NamedQuery(name = "Phase.findAll", query = "SELECT p FROM Phase p")
@Transactional
public class Phase extends MarloBaseEntity<String> implements Serializable {

  private static final long serialVersionUID = 1L;

  @Lob
  private String description;

  private Boolean editable;

  @Temporal(TemporalType.DATE)
  @Column(name = "end_date")
  private Date endDate;

  @Column(name = "global_unit_id")
  private java.math.BigInteger globalUnitId;

  @Lob
  private String name;

  @Temporal(TemporalType.DATE)
  @Column(name = "start_date")
  private Date startDate;

  private Boolean upkeep;

  private Boolean visible;

  private Integer year;

  // bi-directional many-to-one association to DeliverableMetadataElement
  @OneToMany(mappedBy = "phase", fetch = FetchType.LAZY)
  private List<DeliverableMetadataElement> deliverableMetadataElements;

  // bi-directional many-to-one association to Deliverable
  @OneToMany(mappedBy = "phase", fetch = FetchType.LAZY)
  private List<Deliverable> deliverables;

  // bi-directional many-to-one association to Phas
  @JoinColumn(name = "next_phase")
  @ManyToOne
  private Phase phase;

  // bi-directional many-to-one association to Phas
  @OneToMany(mappedBy = "phase", fetch = FetchType.LAZY)
  private List<Phase> phases;

  // bi-directional many-to-one association to ReportSynthesisAltmetric
  @OneToMany(mappedBy = "phase", fetch = FetchType.LAZY)
  private List<ReportSynthesisAltmetric> reportSynthesisAltmetricList;

  public Phase() {
  }

  public Deliverable addDeliverable(Deliverable deliverable) {
    this.getDeliverables().add(deliverable);
    deliverable.setPhase(this);

    return deliverable;
  }

  public DeliverableMetadataElement
    addDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement) {
    this.getDeliverableMetadataElements().add(deliverableMetadataElement);
    deliverableMetadataElement.setPhase(this);

    return deliverableMetadataElement;
  }

  public Phase addPhas(Phase phas) {
    this.getPhases().add(phas);
    phas.setPhase(this);

    return phas;
  }

  public ReportSynthesisAltmetric addReportSynthesisAltmetric(ReportSynthesisAltmetric reportSynthesisAltmetric) {
    this.getReportSynthesisAltmetricList().add(reportSynthesisAltmetric);
    reportSynthesisAltmetric.setPhase(this);

    return reportSynthesisAltmetric;
  }

  public List<DeliverableMetadataElement> getDeliverableMetadataElements() {
    return this.deliverableMetadataElements;
  }

  public List<Deliverable> getDeliverables() {
    return this.deliverables;
  }

  public String getDescription() {
    return this.description;
  }

  public Boolean getEditable() {
    return this.editable;
  }

  public Date getEndDate() {
    return this.endDate;
  }

  public java.math.BigInteger getGlobalUnitId() {
    return this.globalUnitId;
  }

  public String getName() {
    return this.name;
  }

  public Phase getPhase() {
    return this.phase;
  }

  public List<Phase> getPhases() {
    return this.phases;
  }

  public List<ReportSynthesisAltmetric> getReportSynthesisAltmetricList() {
    return reportSynthesisAltmetricList;
  }

  public Date getStartDate() {
    return this.startDate;
  }

  public Boolean getUpkeep() {
    return this.upkeep;
  }

  public Boolean getVisible() {
    return this.visible;
  }

  public Integer getYear() {
    return this.year;
  }

  public Deliverable removeDeliverable(Deliverable deliverable) {
    this.getDeliverables().remove(deliverable);
    deliverable.setPhase(null);

    return deliverable;
  }

  public DeliverableMetadataElement
    removeDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement) {
    this.getDeliverableMetadataElements().remove(deliverableMetadataElement);
    deliverableMetadataElement.setPhase(null);

    return deliverableMetadataElement;
  }

  public Phase removePhas(Phase phas) {
    this.getPhases().remove(phas);
    phas.setPhase(null);

    return phas;
  }

  public ReportSynthesisAltmetric removeReportSynthesisAltmetric(ReportSynthesisAltmetric reportSynthesisAltmetric) {
    this.getReportSynthesisAltmetricList().remove(reportSynthesisAltmetric);
    reportSynthesisAltmetric.setPhase(null);

    return reportSynthesisAltmetric;
  }

  public void setDeliverableMetadataElements(List<DeliverableMetadataElement> deliverableMetadataElements) {
    this.deliverableMetadataElements = deliverableMetadataElements;
  }

  public void setDeliverables(List<Deliverable> deliverables) {
    this.deliverables = deliverables;
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

  public void setName(String name) {
    this.name = name;
  }

  public void setPhase(Phase phas) {
    this.phase = phas;
  }

  public void setPhases(List<Phase> phases) {
    this.phases = phases;
  }

  public void setReportSynthesisAltmetricList(List<ReportSynthesisAltmetric> reportSynthesisAltmetricList) {
    this.reportSynthesisAltmetricList = reportSynthesisAltmetricList;
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

}