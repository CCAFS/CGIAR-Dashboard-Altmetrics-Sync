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


/**
 * The persistent class for the deliverable_metadata_elements database table.
 */
@Entity
@Table(name = "deliverable_metadata_elements")
@NamedQuery(name = "DeliverableMetadataElement.findAll", query = "SELECT d FROM DeliverableMetadataElement d")
@Transactional
public class DeliverableMetadataElement extends MarloBaseEntity<String> implements Serializable {

  private static final long serialVersionUID = 1L;

  @Lob
  @Column(name = "element_value")
  private String elementValue;

  private Boolean hide;

  // bi-directional many-to-one association to Deliverable
  @ManyToOne
  private Deliverable deliverable;

  // bi-directional many-to-one association to MetadataElement
  @ManyToOne
  @JoinColumn(name = "element_id")
  private MetadataElement metadataElement;

  // bi-directional many-to-one association to Phas
  @ManyToOne
  @JoinColumn(name = "id_phase")
  private Phase phase;

  public DeliverableMetadataElement() {
  }


  public Deliverable getDeliverable() {
    return this.deliverable;
  }

  public String getElementValue() {
    return this.elementValue;
  }

  public Boolean getHide() {
    return this.hide;
  }

  public MetadataElement getMetadataElement() {
    return this.metadataElement;
  }

  public Phase getPhase() {
    return this.phase;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setElementValue(String elementValue) {
    this.elementValue = elementValue;
  }

  public void setHide(Boolean hide) {
    this.hide = hide;
  }

  public void setMetadataElement(MetadataElement metadataElement) {
    this.metadataElement = metadataElement;
  }

  public void setPhase(Phase phas) {
    this.phase = phas;
  }

}