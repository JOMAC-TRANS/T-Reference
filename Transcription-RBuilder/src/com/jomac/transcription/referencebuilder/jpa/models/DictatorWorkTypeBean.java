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

@Entity
@Table(name = "DICTATORWORKTYPES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DictatorWorkTypeBean.findAll", query = "SELECT d FROM DictatorWorkTypeBean d"),
    @NamedQuery(name = "DictatorWorkTypeBean.findByDictatorworktypeid", query = "SELECT d FROM DictatorWorkTypeBean d WHERE d.dictatorworktypeid = :dictatorworktypeid"),
    @NamedQuery(name = "DictatorWorkTypeBean.findByDictator&WorkTYpe",
            query = "SELECT d FROM DictatorWorkTypeBean d WHERE (d.dictatorid.dictatorid = :dictatorid AND d.worktypeid.worktypeid = :worktypeid)")})
public class DictatorWorkTypeBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "DICTATORWORKTYPEID")
    private Integer dictatorworktypeid;
    @JoinColumn(name = "WORKTYPEID", referencedColumnName = "WORKTYPEID")
    @ManyToOne(optional = false)
    private WorkTypeBean worktypeid;
    @JoinColumn(name = "DICTATORID", referencedColumnName = "DICTATORID")
    @ManyToOne(optional = false)
    private DictatorBean dictatorid;

    public DictatorWorkTypeBean() {
    }

    public DictatorWorkTypeBean(Integer dictatorworktypeid) {
        this.dictatorworktypeid = dictatorworktypeid;
    }

    public Integer getDictatorworktypeid() {
        return dictatorworktypeid;
    }

    public void setDictatorworktypeid(Integer dictatorworktypeid) {
        this.dictatorworktypeid = dictatorworktypeid;
    }

    public WorkTypeBean getWorktypeid() {
        return worktypeid;
    }

    public void setWorktypeid(WorkTypeBean worktypeid) {
        this.worktypeid = worktypeid;
    }

    public DictatorBean getDictatorid() {
        return dictatorid;
    }

    public void setDictatorid(DictatorBean dictatorid) {
        this.dictatorid = dictatorid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dictatorworktypeid != null ? dictatorworktypeid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DictatorWorkTypeBean)) {
            return false;
        }
        DictatorWorkTypeBean other = (DictatorWorkTypeBean) object;
        if ((this.dictatorworktypeid == null && other.dictatorworktypeid != null) || (this.dictatorworktypeid != null && !this.dictatorworktypeid.equals(other.dictatorworktypeid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jomac.transcription.referencebuilder.jpa.models.DictatorWorkTypeBean[ dictatorworktypeid=" + dictatorworktypeid + " ]";
    }
}
