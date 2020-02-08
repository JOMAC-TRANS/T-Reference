package com.jomac.transcription.activator.jpa.models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "registrations")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegistrationBean.findAll", query = "SELECT r FROM RegistrationBean r"),
    @NamedQuery(name = "RegistrationBean.findRequestBy", query = "SELECT r FROM RegistrationBean r WHERE r.active = :active"),
    @NamedQuery(name = "RegistrationBean.findAllActiveRequest", query = "SELECT r FROM RegistrationBean r WHERE r.active = true"),
    @NamedQuery(name = "RegistrationBean.findAllActiveByPerson", query = "SELECT r FROM RegistrationBean r "
            + "WHERE r.active = true AND (LOWER (r.personid.name) LIKE LOWER(:query))"),
    @NamedQuery(name = "RegistrationBean.findAllActiveByQuery", query = "SELECT r FROM RegistrationBean r "
            + "WHERE r.active = true AND (LOWER (r.personid.name) LIKE LOWER(:query) OR LOWER (r.productid.name) LIKE LOWER(:query)"
            + "OR LOWER (r.productid.version) LIKE LOWER(:query))"),
    @NamedQuery(name = "RegistrationBean.findAllNewRequest", query = "SELECT r FROM RegistrationBean r WHERE r.active IS NULL"),
    @NamedQuery(name = "RegistrationBean.findUpdateRequest", query = "SELECT r FROM RegistrationBean r WHERE r.active = TRUE "
            + "AND r.productid.productrequest != r.productid.productvalue"),
    @NamedQuery(name = "RegistrationBean.findAllRequest", query = "SELECT r FROM RegistrationBean r WHERE r.active = FALSE OR r.active IS NULL"),
    @NamedQuery(name = "RegistrationBean.findByExpiration", query = "SELECT r FROM RegistrationBean r WHERE r.active = TRUE "
            + "AND r.expirationdate <= :expirationdate"),
    @NamedQuery(name = "RegistrationBean.findAbandonAccount", query = "SELECT r FROM RegistrationBean r "
            + "WHERE r.active = TRUE AND r.lastlogin <= :date1 OR r.registrationdate <= :date1 AND r.lastlogin IS NULL"),
    @NamedQuery(name = "RegistrationBean.findByActive", query = "SELECT r FROM RegistrationBean r WHERE r.active = :active")})
public class RegistrationBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "registrationid")
    private Integer registrationid;
    @Column(name = "registrationdate")
    @Temporal(TemporalType.DATE)
    private Date registrationdate;
    @Column(name = "expirationdate")
    @Temporal(TemporalType.DATE)
    private Date expirationdate;
    @Column(name = "lastlogin")
    @Temporal(TemporalType.DATE)
    private Date lastlogin;
    @Column(name = "active")
    private Boolean active;
    @JoinColumn(name = "productid", referencedColumnName = "productid")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private ProductBean productid;
    @JoinColumn(name = "personid", referencedColumnName = "personid")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private PersonBean personid;
    @JoinColumn(name = "machineid", referencedColumnName = "machineid")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private MachineBean machineid;

    public RegistrationBean() {
    }

    public RegistrationBean(Integer registrationid) {
        this.registrationid = registrationid;
    }

    public Integer getRegistrationid() {
        return registrationid;
    }

    public void setRegistrationid(Integer registrationid) {
        this.registrationid = registrationid;
    }

    public Date getRegistrationdate() {
        return registrationdate;
    }

    public void setRegistrationdate(Date registrationdate) {
        this.registrationdate = registrationdate;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ProductBean getProductid() {
        return productid;
    }

    public void setProductid(ProductBean productid) {
        this.productid = productid;
    }

    public PersonBean getPersonid() {
        return personid;
    }

    public void setPersonid(PersonBean personid) {
        this.personid = personid;
    }

    public MachineBean getMachineid() {
        return machineid;
    }

    public void setMachineid(MachineBean machineid) {
        this.machineid = machineid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (registrationid != null ? registrationid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RegistrationBean)) {
            return false;
        }
        RegistrationBean other = (RegistrationBean) object;
        if ((this.registrationid == null && other.registrationid != null) || (this.registrationid != null && !this.registrationid.equals(other.registrationid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jomac.transcription.activator.jpa.models.RegistrationBean[ registrationid=" + registrationid + " ]";
    }
}
