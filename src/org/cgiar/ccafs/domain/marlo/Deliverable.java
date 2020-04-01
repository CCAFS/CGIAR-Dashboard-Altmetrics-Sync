package org.cgiar.ccafs.domain.marlo;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigInteger;
import java.util.List;


/**
 * The persistent class for the deliverables database table.
 * 
 */
@Entity
@Table(name="deliverables")
@NamedQuery(name="Deliverable.findAll", query="SELECT d FROM Deliverable d")
public class Deliverable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	@Column(name="active_since")
	private Timestamp activeSince;

	@Column(name="create_date")
	private Timestamp createDate;

	@Column(name="created_by")
	private BigInteger createdBy;

	@Column(name="global_unit_id")
	private BigInteger globalUnitId;

	@Column(name="is_active")
	private byte isActive;

	@Column(name="is_publication")
	private byte isPublication;

	@Lob
	@Column(name="modification_justification")
	private String modificationJustification;

	@Column(name="modified_by")
	private BigInteger modifiedBy;

	@Column(name="project_id")
	private BigInteger projectId;

	//bi-directional many-to-one association to DeliverableMetadataElement
	@OneToMany(mappedBy="deliverable")
	private List<DeliverableMetadataElement> deliverableMetadataElements;

	//bi-directional many-to-one association to Phas
	@ManyToOne
	@JoinColumn(name="id_phase")
	private Phase phase;

	public Deliverable() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Timestamp getActiveSince() {
		return this.activeSince;
	}

	public void setActiveSince(Timestamp activeSince) {
		this.activeSince = activeSince;
	}

	public Timestamp getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public BigInteger getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(BigInteger createdBy) {
		this.createdBy = createdBy;
	}

	public BigInteger getGlobalUnitId() {
		return this.globalUnitId;
	}

	public void setGlobalUnitId(BigInteger globalUnitId) {
		this.globalUnitId = globalUnitId;
	}

	public byte getIsActive() {
		return this.isActive;
	}

	public void setIsActive(byte isActive) {
		this.isActive = isActive;
	}

	public byte getIsPublication() {
		return this.isPublication;
	}

	public void setIsPublication(byte isPublication) {
		this.isPublication = isPublication;
	}

	public String getModificationJustification() {
		return this.modificationJustification;
	}

	public void setModificationJustification(String modificationJustification) {
		this.modificationJustification = modificationJustification;
	}

	public BigInteger getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(BigInteger modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public BigInteger getProjectId() {
		return this.projectId;
	}

	public void setProjectId(BigInteger projectId) {
		this.projectId = projectId;
	}

	public List<DeliverableMetadataElement> getDeliverableMetadataElements() {
		return this.deliverableMetadataElements;
	}

	public void setDeliverableMetadataElements(List<DeliverableMetadataElement> deliverableMetadataElements) {
		this.deliverableMetadataElements = deliverableMetadataElements;
	}

	public DeliverableMetadataElement addDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement) {
		getDeliverableMetadataElements().add(deliverableMetadataElement);
		deliverableMetadataElement.setDeliverable(this);

		return deliverableMetadataElement;
	}

	public DeliverableMetadataElement removeDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement) {
		getDeliverableMetadataElements().remove(deliverableMetadataElement);
		deliverableMetadataElement.setDeliverable(null);

		return deliverableMetadataElement;
	}

	public Phase getPhase() {
		return this.phase;
	}

	public void setPhase(Phase phas) {
		this.phase = phas;
	}

}