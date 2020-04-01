package org.cgiar.ccafs.domain.marlo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the metadata_elements database table.
 * 
 */
@Entity
@Table(name="metadata_elements")
@NamedQuery(name="MetadataElement.findAll", query="SELECT m FROM MetadataElement m")
public class MetadataElement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	@Lob
	private String definitation;

	@Column(name="econded_name")
	private String econdedName;

	private String element;

	private String qualifier;

	private String schema;

	private String status;

	private String vocabulary;

	//bi-directional many-to-one association to DeliverableMetadataElement
	@OneToMany(mappedBy="metadataElement")
	private List<DeliverableMetadataElement> deliverableMetadataElements;

	public MetadataElement() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDefinitation() {
		return this.definitation;
	}

	public void setDefinitation(String definitation) {
		this.definitation = definitation;
	}

	public String getEcondedName() {
		return this.econdedName;
	}

	public void setEcondedName(String econdedName) {
		this.econdedName = econdedName;
	}

	public String getElement() {
		return this.element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public String getQualifier() {
		return this.qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getSchema() {
		return this.schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVocabulary() {
		return this.vocabulary;
	}

	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}

	public List<DeliverableMetadataElement> getDeliverableMetadataElements() {
		return this.deliverableMetadataElements;
	}

	public void setDeliverableMetadataElements(List<DeliverableMetadataElement> deliverableMetadataElements) {
		this.deliverableMetadataElements = deliverableMetadataElements;
	}

	public DeliverableMetadataElement addDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement) {
		getDeliverableMetadataElements().add(deliverableMetadataElement);
		deliverableMetadataElement.setMetadataElement(this);

		return deliverableMetadataElement;
	}

	public DeliverableMetadataElement removeDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement) {
		getDeliverableMetadataElements().remove(deliverableMetadataElement);
		deliverableMetadataElement.setMetadataElement(null);

		return deliverableMetadataElement;
	}

}