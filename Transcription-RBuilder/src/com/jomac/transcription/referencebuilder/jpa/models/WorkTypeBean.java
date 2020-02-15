package com.jomac.transcription.referencebuilder.jpa.models;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "WORKTYPES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WorkTypeBean.findAll", query = "SELECT w FROM WorkTypeBean w"),
    @NamedQuery(name = "WorkTypeBean.findByWorktypeid", query = "SELECT w FROM WorkTypeBean w WHERE w.worktypeid = :worktypeid"),
    @NamedQuery(name = "WorkTypeBean.findByWorktype", query = "SELECT w FROM WorkTypeBean w WHERE w.worktype = :worktype")})
public class WorkTypeBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "WORKTYPEID")
    private Integer worktypeid;
    @Column(name = "WORKTYPE")
    private String worktype;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "worktypeid")
    private List<DictatorWorkTypeBean> dictatorWorkTypeBeanList;

    public WorkTypeBean() {
    }

    public WorkTypeBean(Integer worktypeid) {
        this.worktypeid = worktypeid;
    }

    public Integer getWorkTypeid() {
        return worktypeid;
    }

    public void setWorkTypeid(Integer worktypeid) {
        this.worktypeid = worktypeid;
    }

    public String getWorkType() {
        return worktype;
    }

    public void setWorktype(String worktype) {
        this.worktype = worktype;
    }

    @XmlTransient
    public List<DictatorWorkTypeBean> getDictatorWorkTypeBeanList() {
        return dictatorWorkTypeBeanList;
    }

    public void setDictatorWorkTypeBeanList(List<DictatorWorkTypeBean> dictatorWorkTypeBeanList) {
        this.dictatorWorkTypeBeanList = dictatorWorkTypeBeanList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (worktypeid != null ? worktypeid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkTypeBean)) {
            return false;
        }
        WorkTypeBean other = (WorkTypeBean) object;
        if ((this.worktypeid == null && other.worktypeid != null) || (this.worktypeid != null && !this.worktypeid.equals(other.worktypeid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jomac.transcription.referencebuilder.jpa.models.WorktypeBean[ worktypeid=" + worktypeid + " ]";
    }
}
