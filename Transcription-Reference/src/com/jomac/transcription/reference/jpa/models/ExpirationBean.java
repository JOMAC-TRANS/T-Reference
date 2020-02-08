package com.jomac.transcription.reference.jpa.models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "EXPIRATION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExpirationBean.findAll", query = "SELECT e FROM ExpirationBean e"),
    @NamedQuery(name = "ExpirationBean.findByExpirationdate", query = "SELECT e FROM ExpirationBean e WHERE e.expirationdate = :expirationdate"),
    @NamedQuery(name = "ExpirationBean.findByFirstusagedate", query = "SELECT e FROM ExpirationBean e WHERE e.firstusagedate = :firstusagedate")})
public class ExpirationBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "EXPIRATIONDATE")
    @Temporal(TemporalType.DATE)
    private Date expirationdate;
    @Column(name = "FIRSTUSAGEDATE")
    @Temporal(TemporalType.DATE)
    private Date firstusagedate;

    public ExpirationBean() {
    }

    public ExpirationBean(Date expirationdate) {
        this.expirationdate = expirationdate;
    }

    public Date getExpirationdate() {
        return expirationdate;
    }

    public void setExpirationdate(Date expirationdate) {
        this.expirationdate = expirationdate;
    }

    public Date getFirstusagedate() {
        return firstusagedate;
    }

    public void setFirstusagedate(Date firstusagedate) {
        this.firstusagedate = firstusagedate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (expirationdate != null ? expirationdate.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExpirationBean)) {
            return false;
        }
        ExpirationBean other = (ExpirationBean) object;
        if ((this.expirationdate == null && other.expirationdate != null) || (this.expirationdate != null && !this.expirationdate.equals(other.expirationdate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jomac.transcription.reference.resources.jpa.models.ExpirationBean[ expirationdate=" + expirationdate + " ]";
    }
}
