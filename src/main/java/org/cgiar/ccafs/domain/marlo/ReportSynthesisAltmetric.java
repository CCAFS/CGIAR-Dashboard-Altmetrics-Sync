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
 * The persistent class for the report_synthesis_altmetrics database table.
 */
@Entity
@Table(name = "report_synthesis_altmetrics")
@NamedQuery(name = "ReportSynthesisAltmetric.findAll", query = "SELECT r FROM ReportSynthesisAltmetric r")
@Transactional
public class ReportSynthesisAltmetric extends MarloBaseEntity<String> implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "attention_score")
  private Integer attentionScore;

  @Lob
  private String author;

  @Column(name = "blog_total")
  private Integer blogTotal;

  @Column(name = "detail_url")
  private String detailUrl;

  private String doi;

  @Column(name = "facebook_total")
  private Integer facebookTotal;

  @ManyToOne
  @JoinColumn(name = "id_phase")
  private Phase phase;

  @Lob
  @Column(name = "journal_title")
  private String journalTitle;

  @Column(name = "mendeley_total")
  private Integer mendeleyTotal;

  @Column(name = "news_total")
  private Integer newsTotal;

  @Column(name = "policy_total")
  private Integer policyTotal;

  // FIXME THIS IS A TEMPORAL SOLUTION. When this is a Temporal field, it throws an Exception...
  @Column(name = "publication_date")
  private String publicationDate;

  @Lob
  private String title;

  @Column(name = "twitter_total")
  private Integer twitterTotal;

  @Column(name = "wikipedia_total")
  private Integer wikipediaTotal;

  public ReportSynthesisAltmetric() {
  }

  public Integer getAttentionScore() {
    return this.attentionScore;
  }

  public String getAuthor() {
    return this.author;
  }

  public Integer getBlogTotal() {
    return this.blogTotal;
  }

  public String getDetailUrl() {
    return this.detailUrl;
  }

  public String getDoi() {
    return this.doi;
  }

  public Integer getFacebookTotal() {
    return this.facebookTotal;
  }

  public String getJournalTitle() {
    return this.journalTitle;
  }

  public Integer getMendeleyTotal() {
    return this.mendeleyTotal;
  }

  public Integer getNewsTotal() {
    return this.newsTotal;
  }

  public Phase getPhase() {
    return this.phase;
  }

  public Integer getPolicyTotal() {
    return this.policyTotal;
  }

  public String getPublicationDate() {
    return this.publicationDate;
  }

  public String getTitle() {
    return this.title;
  }

  public Integer getTwitterTotal() {
    return this.twitterTotal;
  }

  public Integer getWikipediaTotal() {
    return this.wikipediaTotal;
  }

  public void setAttentionScore(Integer attentionScore) {
    this.attentionScore = attentionScore;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public void setBlogTotal(Integer blogTotal) {
    this.blogTotal = blogTotal;
  }

  public void setDetailUrl(String detailUrl) {
    this.detailUrl = detailUrl;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public void setFacebookTotal(Integer facebookTotal) {
    this.facebookTotal = facebookTotal;
  }

  public void setJournalTitle(String journalTitle) {
    this.journalTitle = journalTitle;
  }

  public void setMendeleyTotal(Integer mendeleyTotal) {
    this.mendeleyTotal = mendeleyTotal;
  }

  public void setNewsTotal(Integer newsTotal) {
    this.newsTotal = newsTotal;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setPolicyTotal(Integer policyTotal) {
    this.policyTotal = policyTotal;
  }

  public void setPublicationDate(String publicationDate) {
    this.publicationDate = publicationDate;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setTwitterTotal(Integer twitterTotal) {
    this.twitterTotal = twitterTotal;
  }

  public void setWikipediaTotal(Integer wikipediaTotal) {
    this.wikipediaTotal = wikipediaTotal;
  }

}