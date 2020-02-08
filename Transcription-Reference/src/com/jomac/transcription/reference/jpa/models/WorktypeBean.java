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
    @NamedQuery(name = "WorktypeBean.findAll", query = "SELECT w FROM WorktypeBean w"),
    @NamedQuery(name = "WorktypeBean.findByWorktypeid", query = "SELECT w FROM WorktypeBean w WHERE w.worktypeid = :worktypeid"),
    @NamedQuery(name = "WorktypeBean.findByWorktype", query = "SELECT w FROM WorktypeBean w WHERE w.worktype = :worktype")})
public class WorktypeBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "WORKTYPEID")
    private Integer worktypeid;
    @Column(name = "WORKTYPE")
    private String worktype;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "worktypeid")
    private List<DictatorWorktypeBean> dictatorWorktypeBeanList;

    public WorktypeBean() {
    }

    public WorktypeBean(Integer worktypeid) {
        this.worktypeid = worktypeid;
    }

    public Integer getWorktypeid() {
        return worktypeid;
    }

    public void setWorktypeid(Integer worktypeid) {
        this.worktypeid = worktypeid;
    }

    public String getWorktype() {
        return worktype;
    }

    public void setWorktype(String worktype) {
        this.worktype = worktype;
    }

    @XmlTransient
    public List<DictatorWorktypeBean> getDictatorWorktypeBeanList() {
        return dictatorWorktypeBeanList;
    }

    public void setDictatorWorktypeBeanList(List<DictatorWorktypeBean> dictatorWorktypeBeanList) {
        this.dictatorWorktypeBeanList = dictatorWorktypeBeanList;
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
        if (!(object instanceof WorktypeBean)) {
            return false;
        }
        WorktypeBean other = (WorktypeBean) object;
        if ((this.worktypeid == null && other.worktypeid != null) || (this.worktypeid != null && !this.worktypeid.equals(other.worktypeid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return worktypeid != null ? worktype : "<no value>";
    }
}
