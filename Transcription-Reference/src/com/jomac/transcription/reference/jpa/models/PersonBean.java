package com.jomac.transcription.reference.jpa.models;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "persons")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersonBean.findAll", query = "SELECT p FROM PersonBean p"),
    @NamedQuery(name = "PersonBean.findByPersonid", query = "SELECT p FROM PersonBean p WHERE p.personid = :personid"),
    @NamedQuery(name = "PersonBean.findByName", query = "SELECT p FROM PersonBean p WHERE p.name = :name"),
    @NamedQuery(name = "PersonBean.findByEmail", query = "SELECT p FROM PersonBean p WHERE p.email = :email"),
    @NamedQuery(name = "PersonBean.findByLocation", query = "SELECT p FROM PersonBean p WHERE p.location = :location")})
public class PersonBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "person_sequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "person_sequence", sequenceName = "person_sequence", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "personid")
    private Integer personid;
    @Column(name = "name")
    private String name;
    @Column(name = "phonenumber")
    private String phonenumber;
    @Column(name = "email")
    private String email;
    @Column(name = "location")
    private String location;

    public PersonBean() {
    }

    public PersonBean(Integer personid) {
        this.personid = personid;
    }

    public Integer getPersonid() {
        return personid;
    }

    public void setPersonid(Integer personid) {
        this.personid = personid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (personid != null ? personid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersonBean)) {
            return false;
        }
        PersonBean other = (PersonBean) object;
        if ((this.personid == null && other.personid != null) || (this.personid != null && !this.personid.equals(other.personid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jomac.transcription.reference.jpa.models.PersonBean[ personid=" + personid + " ]";
    }
}
