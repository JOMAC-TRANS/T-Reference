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
@Table(name = "DICTATORS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DictatorBean.findAll", query = "SELECT d FROM DictatorBean d"),
    @NamedQuery(name = "DictatorBean.findByDictatorid", query = "SELECT d FROM DictatorBean d WHERE d.dictatorid = :dictatorid"),
    @NamedQuery(name = "DictatorBean.findByLastname", query = "SELECT d FROM DictatorBean d WHERE d.lastname = :lastname"),
    @NamedQuery(name = "DictatorBean.findByFirstname", query = "SELECT d FROM DictatorBean d WHERE d.firstname = :firstname"),
    @NamedQuery(name = "DictatorBean.findByMiddlename", query = "SELECT d FROM DictatorBean d WHERE d.middlename = :middlename")})
public class DictatorBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "DICTATORID")
    private Integer dictatorid;
    @Column(name = "LASTNAME")
    private String lastname;
    @Column(name = "FIRSTNAME")
    private String firstname;
    @Column(name = "MIDDLENAME")
    private String middlename;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dictatorid")
    private List<DictatorWorkTypeBean> dictatorWorkTypeBeanList;

    public DictatorBean() {
    }

    public DictatorBean(Integer dictatorid) {
        this.dictatorid = dictatorid;
    }

    public Integer getDictatorid() {
        return dictatorid;
    }

    public void setDictatorid(Integer dictatorid) {
        this.dictatorid = dictatorid;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
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
        hash += (dictatorid != null ? dictatorid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DictatorBean)) {
            return false;
        }
        DictatorBean other = (DictatorBean) object;
        if ((this.dictatorid == null && other.dictatorid != null) || (this.dictatorid != null && !this.dictatorid.equals(other.dictatorid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jomac.transcription.referencebuilder.jpa.models.DictatorBean[ dictatorid=" + dictatorid + " ]";
    }
}
