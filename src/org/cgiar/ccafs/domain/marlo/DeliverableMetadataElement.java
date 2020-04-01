package org.cgiar.ccafs.domain.marlo;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the deliverable_metadata_elements database table.
 * 
 */
@Entity
@Table(name="deliverable_metadata_elements")
@NamedQuery(name="DeliverableMetadataElement.findAll", query="SELECT d FROM DeliverableMetadataElement d")
public class DeliverableMetadataElement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	@Lob
	@Column(name="element_value")
	private String elementValue;

	private byte hide;

	//bi-directional many-to-one association to Deliverable
	@ManyToOne
	private Deliverable deliverable;

	//bi-directional many-to-one association to MetadataElement
	@ManyToOne
	@JoinColumn(name="element_id")
	private MetadataElement metadataElement;

	//bi-directional many-to-one association to Phas
	@ManyToOne
	@JoinColumn(name="id_phase")
	private Phase phase;

	public DeliverableMetadataElement() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getElementValue() {
		return this.elementValue;
	}

	public void setElementValue(String elementValue) {
		this.elementValue = elementValue;
	}

	public byte getHide() {
		return this.hide;
	}

	public void setHide(byte hide) {
		this.hide = hide;
	}

	public Deliverable getDeliverable() {
		return this.deliverable;
	}

	public void setDeliverable(Deliverable deliverable) {
		this.deliverable = deliverable;
	}

	public MetadataElement getMetadataElement() {
		return this.metadataElement;
	}

	public void setMetadataElement(MetadataElement metadataElement) {
		this.metadataElement = metadataElement;
	}

	public Phase getPhase() {
		return this.phase;
	}

	public void setPhase(Phase phas) {
		this.phase = phas;
	}

}