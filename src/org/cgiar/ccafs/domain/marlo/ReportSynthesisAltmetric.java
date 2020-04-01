package org.cgiar.ccafs.domain.marlo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.google.gson.GsonBuilder;


/**
 * The persistent class for the report_synthesis_altmetrics database table.
 */
@Entity
@Table(name = "report_synthesis_altmetrics")
@NamedQuery(name = "ReportSynthesisAltmetric.findAll", query = "SELECT r FROM ReportSynthesisAltmetric r")
public class ReportSynthesisAltmetric implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;

  @Column(name = "attention_score")
  private int attentionScore;

  @Lob
  private String author;

  @Column(name = "blog_total")
  private int blogTotal;

  @Column(name = "detail_url")
  private String detailUrl;

  private String doi;

  @Column(name = "facebook_total")
  private int facebookTotal;

  @ManyToOne
  @JoinColumn(name = "id_phase")
  private Phase phase;

  @Lob
  @Column(name = "journal_title")
  private String journalTitle;

  @Column(name = "mendeley_total")
  private int mendeleyTotal;

  @Column(name = "news_total")
  private int newsTotal;

  @Column(name = "policy_total")
  private int policyTotal;

  // FIXME THIS IS A TEMPORAL SOLUTION. When this is a Temporal field, it throws an Exception...
  @Column(name = "publication_date")
  private String publicationDate;

  @Lob
  private String title;

  @Column(name = "twitter_total")
  private int twitterTotal;

  @Column(name = "wikipedia_total")
  private int wikipediaTotal;

  public ReportSynthesisAltmetric() {
  }

  public int getAttentionScore() {
    return this.attentionScore;
  }

  public String getAuthor() {
    return this.author;
  }

  public int getBlogTotal() {
    return this.blogTotal;
  }

  public String getDetailUrl() {
    return this.detailUrl;
  }

  public String getDoi() {
    return this.doi;
  }

  public int getFacebookTotal() {
    return this.facebookTotal;
  }

  public String getId() {
    return this.id;
  }

  public String getJournalTitle() {
    return this.journalTitle;
  }

  public int getMendeleyTotal() {
    return this.mendeleyTotal;
  }

  public int getNewsTotal() {
    return this.newsTotal;
  }

  public Phase getPhase() {
    return this.phase;
  }

  public int getPolicyTotal() {
    return this.policyTotal;
  }

  public String getPublicationDate() {
    return this.publicationDate;
  }

  public String getTitle() {
    return this.title;
  }

  public int getTwitterTotal() {
    return this.twitterTotal;
  }

  public int getWikipediaTotal() {
    return this.wikipediaTotal;
  }

  public void setAttentionScore(int attentionScore) {
    this.attentionScore = attentionScore;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public void setBlogTotal(int blogTotal) {
    this.blogTotal = blogTotal;
  }

  public void setDetailUrl(String detailUrl) {
    this.detailUrl = detailUrl;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public void setFacebookTotal(int facebookTotal) {
    this.facebookTotal = facebookTotal;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setJournalTitle(String journalTitle) {
    this.journalTitle = journalTitle;
  }

  public void setMendeleyTotal(int mendeleyTotal) {
    this.mendeleyTotal = mendeleyTotal;
  }

  public void setNewsTotal(int newsTotal) {
    this.newsTotal = newsTotal;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setPolicyTotal(int policyTotal) {
    this.policyTotal = policyTotal;
  }

  public void setPublicationDate(String publicationDate) {
    this.publicationDate = publicationDate;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setTwitterTotal(int twitterTotal) {
    this.twitterTotal = twitterTotal;
  }

  public void setWikipediaTotal(int wikipediaTotal) {
    this.wikipediaTotal = wikipediaTotal;
  }

  @Override
  public String toString() {
    return new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(this);
  }

}