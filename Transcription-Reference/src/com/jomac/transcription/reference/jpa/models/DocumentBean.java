package com.jomac.transcription.reference.jpa.models;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "DOCUMENTS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DocumentBean.findByDocumentid", query = "SELECT d FROM DocumentBean d "
            + "WHERE d.documentid = :documentid"),
    @NamedQuery(name = "DocumentBean.findByDocument", query = "SELECT d FROM DocumentBean d "
            + "WHERE LOWER(d.document) LIKE :document"),
    @NamedQuery(name = "DocumentBean.findByDictator", query = "SELECT d FROM DocumentBean d "
            + "WHERE d.dictatorworktypeid.dictatorid.dictatorid = :dictatorid AND LOWER(d.document) LIKE :document"),
    @NamedQuery(name = "DocumentBean.findByWorkType", query = "SELECT d FROM DocumentBean d "
            + "WHERE d.dictatorworktypeid.worktypeid.worktype LIKE :worktype AND LOWER(d.document) LIKE :document"),
    @NamedQuery(name = "DocumentBean.findByDictator&WorkType", query = "SELECT d FROM DocumentBean d "
            + "WHERE (d.dictatorworktypeid.dictatorid.dictatorid = :dictatorid AND "
            + "d.dictatorworktypeid.worktypeid.worktype LIKE :worktype) AND LOWER(d.document) LIKE :document"),
    @NamedQuery(name = "DocumentBean.findByDocument_", query = "SELECT d FROM DocumentBean d "
            + "WHERE (LOWER(d.document) LIKE :document OR LOWER(d.document) LIKE :document2)"),
    @NamedQuery(name = "DocumentBean.findByDictator_", query = "SELECT d FROM DocumentBean d "
            + "WHERE d.dictatorworktypeid.dictatorid.dictatorid = :dictatorid AND (LOWER(d.document) LIKE :document "
            + "OR LOWER(d.document) LIKE :document2)"),
    @NamedQuery(name = "DocumentBean.findByWorkType_", query = "SELECT d FROM DocumentBean d "
            + "WHERE d.dictatorworktypeid.worktypeid.worktype LIKE :worktype AND (LOWER(d.document) LIKE :document "
            + "OR LOWER(d.document) LIKE :document2)"),
    @NamedQuery(name = "DocumentBean.findByDictator&WorkType_", query = "SELECT d FROM DocumentBean d "
            + "WHERE (d.dictatorworktypeid.dictatorid.dictatorid = :dictatorid AND "
            + "d.dictatorworktypeid.worktypeid.worktype LIKE :worktype) AND (LOWER(d.document) LIKE :document "
            + "OR LOWER(d.document) LIKE :document2)")})
public class DocumentBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "DOCUMENTID")
    private Integer documentid;
    @Column(name = "DOCUMENT")
    private String document;
    @JoinColumn(name = "DICTATORWORKTYPEID", referencedColumnName = "DICTATORWORKTYPEID")
    @ManyToOne(optional = false)
    private DictatorWorktypeBean dictatorworktypeid;
    @Transient
    private Integer searchCount;
    @Transient
    private Integer caretPosition;
    @Transient
    private String documentHTML;

    public DocumentBean() {
    }

    public DocumentBean(Integer documentid) {
        this.documentid = documentid;
    }

    public Integer getDocumentid() {
        return documentid;
    }

    public void setDocumentid(Integer documentid) {
        this.documentid = documentid;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public DictatorWorktypeBean getDictatorworktypeid() {
        return dictatorworktypeid;
    }

    public void setDictatorworktypeid(DictatorWorktypeBean dictatorworktypeid) {
        this.dictatorworktypeid = dictatorworktypeid;
    }

    public Integer getSearchCount() {
        return searchCount == null ? 0 : searchCount;
    }

    public void setSearchCount(Integer searchCount) {
        this.searchCount = searchCount;
    }

    public Integer getCaretPosition() {
        return caretPosition == null ? 0 : caretPosition;
    }

    public void setCaretPosition(Integer caretPosition) {
        this.caretPosition = caretPosition;
    }

    public String getDocumentHTML() {
        return documentHTML == null ? "" : documentHTML;
    }

    public void setDocumentHTML(String documentHTML) {
        this.documentHTML = documentHTML;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (documentid != null ? documentid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DocumentBean)) {
            return false;
        }
        DocumentBean other = (DocumentBean) object;
        if ((this.documentid == null && other.documentid != null) || (this.documentid != null && !this.documentid.equals(other.documentid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return documentid == null ? "" : document;
    }
}
