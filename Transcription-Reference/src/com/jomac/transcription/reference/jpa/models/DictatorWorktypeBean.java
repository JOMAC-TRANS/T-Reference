package com.jomac.transcription.reference.jpa.models;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "DICTATORWORKTYPES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DictatorWorktypeBean.findAll", query = "SELECT d FROM DictatorWorktypeBean d"),
    @NamedQuery(name = "DictatorWorktypeBean.findByDictatorworktypeid", query = "SELECT d FROM DictatorWorktypeBean d WHERE d.dictatorworktypeid = :dictatorworktypeid"),
    @NamedQuery(name = "DictatorWorktypeBean.findWorkTypeByDictator", query = "SELECT d FROM DictatorWorktypeBean d WHERE d.dictatorid = :dictatorid")})
public class DictatorWorktypeBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "DICTATORWORKTYPEID")
    private Integer dictatorworktypeid;
    @JoinColumn(name = "WORKTYPEID", referencedColumnName = "WORKTYPEID")
    @ManyToOne(optional = false)
    private WorktypeBean worktypeid;
    @JoinColumn(name = "DICTATORID", referencedColumnName = "DICTATORID")
    @ManyToOne(optional = false)
    private DictatorBean dictatorid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dictatorworktypeid")
    private List<DocumentBean> documentBeanList;

    public DictatorWorktypeBean() {
    }

    public DictatorWorktypeBean(Integer dictatorworktypeid) {
        this.dictatorworktypeid = dictatorworktypeid;
    }

    public Integer getDictatorworktypeid() {
        return dictatorworktypeid;
    }

    public void setDictatorworktypeid(Integer dictatorworktypeid) {
        this.dictatorworktypeid = dictatorworktypeid;
    }

    public WorktypeBean getWorktypeid() {
        return worktypeid;
    }

    public void setWorktypeid(WorktypeBean worktypeid) {
        this.worktypeid = worktypeid;
    }

    public DictatorBean getDictatorid() {
        return dictatorid;
    }

    public void setDictatorid(DictatorBean dictatorid) {
        this.dictatorid = dictatorid;
    }

    @XmlTransient
    public List<DocumentBean> getDocumentBeanList() {
        return documentBeanList;
    }

    public void setDocumentBeanList(List<DocumentBean> documentBeanList) {
        this.documentBeanList = documentBeanList;
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
        if (!(object instanceof DictatorWorktypeBean)) {
            return false;
        }
        DictatorWorktypeBean other = (DictatorWorktypeBean) object;
        if ((this.dictatorworktypeid == null && other.dictatorworktypeid != null) || (this.dictatorworktypeid != null && !this.dictatorworktypeid.equals(other.dictatorworktypeid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jomac.transcription.reference.jpa.models.DictatorWorktypeBean[ dictatorworktypeid=" + dictatorworktypeid + " ]";
    }
}
