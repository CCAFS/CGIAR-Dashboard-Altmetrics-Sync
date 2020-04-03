package org.cgiar.ccafs.domain.marlo;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
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

import org.springframework.transaction.annotation.Transactional;


/**
 * The persistent class for the deliverables database table.
 */
@Entity
@Table(name = "deliverables")
@NamedQuery(name = "Deliverable.findAll", query = "SELECT d FROM Deliverable d")
@Transactional
public class Deliverable extends MarloBaseEntity<String> implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "active_since")
  private Timestamp activeSince;

  @Column(name = "create_date")
  private Timestamp createDate;

  @Column(name = "created_by")
  private BigInteger createdBy;

  @Column(name = "global_unit_id")
  private BigInteger globalUnitId;

  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "is_publication")
  private Boolean isPublication;

  @Lob
  @Column(name = "modification_justification")
  private String modificationJustification;

  @Column(name = "modified_by")
  private BigInteger modifiedBy;

  @Column(name = "project_id")
  private BigInteger projectId;

  // bi-directional many-to-one association to DeliverableMetadataElement
  @OneToMany(mappedBy = "deliverable", fetch = FetchType.LAZY)
  private List<DeliverableMetadataElement> deliverableMetadataElements;

  // bi-directional many-to-one association to Phas
  @ManyToOne
  @JoinColumn(name = "id_phase")
  private Phase phase;

  public Deliverable() {
  }

  public DeliverableMetadataElement
    addDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement) {
    this.getDeliverableMetadataElements().add(deliverableMetadataElement);
    deliverableMetadataElement.setDeliverable(this);

    return deliverableMetadataElement;
  }

  public Timestamp getActiveSince() {
    return this.activeSince;
  }

  public Timestamp getCreateDate() {
    return this.createDate;
  }

  public BigInteger getCreatedBy() {
    return this.createdBy;
  }

  public List<DeliverableMetadataElement> getDeliverableMetadataElements() {
    return this.deliverableMetadataElements;
  }

  public BigInteger getGlobalUnitId() {
    return this.globalUnitId;
  }

  public Boolean getIsActive() {
    return this.isActive;
  }

  public Boolean getIsPublication() {
    return this.isPublication;
  }

  public String getModificationJustification() {
    return this.modificationJustification;
  }

  public BigInteger getModifiedBy() {
    return this.modifiedBy;
  }

  public Phase getPhase() {
    return this.phase;
  }

  public BigInteger getProjectId() {
    return this.projectId;
  }

  public DeliverableMetadataElement
    removeDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement) {
    this.getDeliverableMetadataElements().remove(deliverableMetadataElement);
    deliverableMetadataElement.setDeliverable(null);

    return deliverableMetadataElement;
  }

  public void setActiveSince(Timestamp activeSince) {
    this.activeSince = activeSince;
  }

  public void setCreateDate(Timestamp createDate) {
    this.createDate = createDate;
  }

  public void setCreatedBy(BigInteger createdBy) {
    this.createdBy = createdBy;
  }

  public void setDeliverableMetadataElements(List<DeliverableMetadataElement> deliverableMetadataElements) {
    this.deliverableMetadataElements = deliverableMetadataElements;
  }

  public void setGlobalUnitId(BigInteger globalUnitId) {
    this.globalUnitId = globalUnitId;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public void setIsPublication(Boolean isPublication) {
    this.isPublication = isPublication;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(BigInteger modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setPhase(Phase phas) {
    this.phase = phas;
  }

  public void setProjectId(BigInteger projectId) {
    this.projectId = projectId;
  }

}