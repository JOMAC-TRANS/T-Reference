package com.jomac.transcription.referencebuilder.jpa.models;

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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author denrosssalenga
 */
@Entity
@Table(name = "DOCUMENTS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DocumentBean.findAll", query = "SELECT d FROM DocumentBean d"),
    @NamedQuery(name = "DocumentBean.findByDocumentid", query = "SELECT d FROM DocumentBean d WHERE d.documentid = :documentid"),
    @NamedQuery(name = "DocumentBean.findByDocument", query = "SELECT d FROM DocumentBean d WHERE d.document = :document")})
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
    private DictatorWorkTypeBean dictatorworktypeid;

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

    public DictatorWorkTypeBean getDictatorworktypeid() {
        return dictatorworktypeid;
    }

    public void setDictatorworktypeid(DictatorWorkTypeBean dictatorworktypeid) {
        this.dictatorworktypeid = dictatorworktypeid;
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
        return "com.jomac.transcription.referencebuilder.jpa.models.DocumentBean[ documentid=" + documentid + " ]";
    }

}
