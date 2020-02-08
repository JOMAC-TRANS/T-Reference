package com.jomac.transcription.reference.jpa.models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "ACTIVATORS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ActivatorBean.findAll", query = "SELECT a FROM ActivatorBean a"),
    @NamedQuery(name = "ActivatorBean.findByActivatorid", query = "SELECT a FROM ActivatorBean a WHERE a.activatorid = :activatorid"),
    @NamedQuery(name = "ActivatorBean.findByFullname", query = "SELECT a FROM ActivatorBean a WHERE a.fullname = :fullname"),
    @NamedQuery(name = "ActivatorBean.findByRegistrationid", query = "SELECT a FROM ActivatorBean a WHERE a.registrationid = :registrationid"),
    @NamedQuery(name = "ActivatorBean.findByPlugins", query = "SELECT a FROM ActivatorBean a WHERE a.plugins = :plugins"),
    @NamedQuery(name = "ActivatorBean.findByExpirationdate", query = "SELECT a FROM ActivatorBean a WHERE a.expirationdate = :expirationdate"),
    @NamedQuery(name = "ActivatorBean.findByActivated", query = "SELECT a FROM ActivatorBean a WHERE a.activated = :activated")})
public class ActivatorBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ACTIVATORID")
    private Integer activatorid;
    @Column(name = "FULLNAME")
    private String fullname;
    @Column(name = "REGISTRATIONID")
    private Integer registrationid;
    @Column(name = "PLUGINS")
    private Integer plugins;
    @Column(name = "EXPIRATIONDATE")
    @Temporal(TemporalType.DATE)
    private Date expirationdate;
    @Column(name = "LASTLOGIN")
    @Temporal(TemporalType.DATE)
    private Date lastlogin;
    @Column(name = "ACTIVATED")
    private boolean activated;

    public ActivatorBean() {
    }

    public ActivatorBean(Integer activatorid) {
        this.activatorid = activatorid;
    }

    public Integer getActivatorid() {
        return activatorid;
    }

    public void setActivatorid(Integer activatorid) {
        this.activatorid = activatorid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Integer getRegistrationid() {
        return registrationid;
    }

    public void setRegistrationid(Integer registrationid) {
        this.registrationid = registrationid;
    }

    public Integer getPlugins() {
        return plugins;
    }

    public void setPlugins(Integer plugins) {
        this.plugins = plugins;
    }

    public Date getExpirationdate() {
        return expirationdate;
    }

    public void setExpirationdate(Date expirationdate) {
        this.expirationdate = expirationdate;
    }

    public Date getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(Date lastlogin) {
        this.lastlogin = lastlogin;
    }

    public boolean getActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (activatorid != null ? activatorid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ActivatorBean)) {
            return false;
        }
        ActivatorBean other = (ActivatorBean) object;
        if ((this.activatorid == null && other.activatorid != null) || (this.activatorid != null && !this.activatorid.equals(other.activatorid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jomac.transcription.reference.jpa.models.ActivatorBean[ activatorid=" + activatorid + " ]";
    }
}
