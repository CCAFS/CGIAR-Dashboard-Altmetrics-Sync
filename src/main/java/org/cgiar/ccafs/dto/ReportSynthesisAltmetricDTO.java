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

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ReportSynthesisAltmetricDTO {

  @Expose
  private String id;

  @Expose
  private Integer attentionScore;

  @Expose
  private String author;

  @Expose
  private Integer blogTotal;

  @Expose
  private String detailUrl;

  @Expose
  private String doi;

  @Expose
  private Integer facebookTotal;

  @Expose
  private PhaseDTO phaseDTO;

  @Expose
  private String journalTitle;

  @Expose
  private Integer mendeleyTotal;

  @Expose
  private Integer newsTotal;

  @Expose
  private Integer policyTotal;

  @Expose
  private String publicationDate;

  @Expose
  private String title;

  @Expose
  private Integer twitterTotal;

  @Expose
  private Integer wikipediaTotal;


  public ReportSynthesisAltmetricDTO() {
  }


  public Integer getAttentionScore() {
    return attentionScore;
  }

  public String getAuthor() {
    return author;
  }

  public Integer getBlogTotal() {
    return blogTotal;
  }

  public String getDetailUrl() {
    return detailUrl;
  }

  public String getDoi() {
    return doi;
  }

  public Integer getFacebookTotal() {
    return facebookTotal;
  }

  public String getId() {
    return id;
  }

  public String getJournalTitle() {
    return journalTitle;
  }

  public Integer getMendeleyTotal() {
    return mendeleyTotal;
  }

  public Integer getNewsTotal() {
    return newsTotal;
  }

  public PhaseDTO getPhaseDTO() {
    return phaseDTO;
  }

  public Integer getPolicyTotal() {
    return policyTotal;
  }

  public String getPublicationDate() {
    return publicationDate;
  }

  public String getTitle() {
    return title;
  }

  public Integer getTwitterTotal() {
    return twitterTotal;
  }

  public Integer getWikipediaTotal() {
    return wikipediaTotal;
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

  public void setId(String id) {
    this.id = id;
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

  public void setPhaseDTO(PhaseDTO phaseDTO) {
    this.phaseDTO = phaseDTO;
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

  @Override
  public String toString() {
    return new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create()
      .toJson(this);
  }

}
