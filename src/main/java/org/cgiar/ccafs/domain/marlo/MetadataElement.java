package org.cgiar.ccafs.domain.marlo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.transaction.annotation.Transactional;


/**
 * The persistent class for the metadata_elements database table.
 */
@Entity
@Table(name = "metadata_elements")
@NamedQuery(name = "MetadataElement.findAll", query = "SELECT m FROM MetadataElement m")
@Transactional
public class MetadataElement extends MarloBaseEntity<String> implements Serializable {

  private static final long serialVersionUID = 1L;

  @Lob
  private String definitation;

  @Column(name = "econded_name")
  private String econdedName;

  private String element;

  private String qualifier;

  private String schema;

  private String status;

  private String vocabulary;

  // bi-directional many-to-one association to DeliverableMetadataElement
  @OneToMany(mappedBy = "metadataElement", fetch = FetchType.LAZY)
  private List<DeliverableMetadataElement> deliverableMetadataElements;

  public MetadataElement() {
  }


  public DeliverableMetadataElement
    addDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement) {
    this.getDeliverableMetadataElements().add(deliverableMetadataElement);
    deliverableMetadataElement.setMetadataElement(this);

    return deliverableMetadataElement;
  }

  public String getDefinitation() {
    return this.definitation;
  }

  public List<DeliverableMetadataElement> getDeliverableMetadataElements() {
    return this.deliverableMetadataElements;
  }

  public String getEcondedName() {
    return this.econdedName;
  }

  public String getElement() {
    return this.element;
  }

  public String getQualifier() {
    return this.qualifier;
  }

  public String getSchema() {
    return this.schema;
  }

  public String getStatus() {
    return this.status;
  }

  public String getVocabulary() {
    return this.vocabulary;
  }

  public DeliverableMetadataElement
    removeDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement) {
    this.getDeliverableMetadataElements().remove(deliverableMetadataElement);
    deliverableMetadataElement.setMetadataElement(null);

    return deliverableMetadataElement;
  }

  public void setDefinitation(String definitation) {
    this.definitation = definitation;
  }

  public void setDeliverableMetadataElements(List<DeliverableMetadataElement> deliverableMetadataElements) {
    this.deliverableMetadataElements = deliverableMetadataElements;
  }

  public void setEcondedName(String econdedName) {
    this.econdedName = econdedName;
  }

  public void setElement(String element) {
    this.element = element;
  }

  public void setQualifier(String qualifier) {
    this.qualifier = qualifier;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setVocabulary(String vocabulary) {
    this.vocabulary = vocabulary;
  }

}