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
@Table(name = "SPECIFICS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SpecificBean.findAll", query = "SELECT s FROM SpecificBean s"),
    @NamedQuery(name = "SpecificBean.findBySpecificid", query = "SELECT s FROM SpecificBean s WHERE s.specificid = :specificid"),
    @NamedQuery(name = "SpecificBean.findByDocument", query = "SELECT s FROM SpecificBean s WHERE s.document = :document")})
public class SpecificBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SPECIFICID")
    private Integer specificid;
    @Column(name = "DOCUMENT")
    private String document;
    @JoinColumn(name = "DICTATORWORKTYPEID", referencedColumnName = "DICTATORWORKTYPEID")
    @ManyToOne
    private DictatorWorkTypeBean dictatorworktypeid;

    public SpecificBean() {
    }

    public SpecificBean(Integer specificid) {
        this.specificid = specificid;
    }

    public Integer getSpecificid() {
        return specificid;
    }

    public void setSpecificid(Integer specificid) {
        this.specificid = specificid;
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
        hash += (specificid != null ? specificid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SpecificBean)) {
            return false;
        }
        SpecificBean other = (SpecificBean) object;
        if ((this.specificid == null && other.specificid != null) || (this.specificid != null && !this.specificid.equals(other.specificid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jomac.transcription.referencebuilder.jpa.models.SpecificBean[ specificid=" + specificid + " ]";
    }

}
