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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author denrosssalenga
 */
@Entity
@Table(name = "machines")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MachineBean.findAll", query = "SELECT m FROM MachineBean m"),
    @NamedQuery(name = "MachineBean.findByMachineid", query = "SELECT m FROM MachineBean m WHERE m.machineid = :machineid"),
    @NamedQuery(name = "MachineBean.findByOsversion", query = "SELECT m FROM MachineBean m WHERE m.osversion = :osversion"),
    @NamedQuery(name = "MachineBean.findByJavaversion", query = "SELECT m FROM MachineBean m WHERE m.javaversion = :javaversion"),
    @NamedQuery(name = "MachineBean.findByProfilename", query = "SELECT m FROM MachineBean m WHERE m.profilename = :profilename"),
    @NamedQuery(name = "MachineBean.findByComputername", query = "SELECT m FROM MachineBean m WHERE m.computername = :computername"),
    @NamedQuery(name = "MachineBean.findByHarddisk", query = "SELECT m FROM MachineBean m WHERE m.harddisk = :harddisk"),
    @NamedQuery(name = "MachineBean.findByMotherboard", query = "SELECT m FROM MachineBean m WHERE m.motherboard = :motherboard")})
public class MachineBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "machine_sequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "machine_sequence", sequenceName = "machine_sequence", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "machineid")
    private Integer machineid;
    @Column(name = "osversion")
    private String osversion;
    @Column(name = "javaversion")
    private String javaversion;
    @Column(name = "profilename")
    private String profilename;
    @Column(name = "computername")
    private String computername;
    @Column(name = "harddisk")
    private String harddisk;
    @Column(name = "motherboard")
    private String motherboard;

    public MachineBean() {
    }

    public MachineBean(Integer machineid) {
        this.machineid = machineid;
    }

    public Integer getMachineid() {
        return machineid;
    }

    public void setMachineid(Integer machineid) {
        this.machineid = machineid;
    }

    public String getOsversion() {
        return osversion;
    }

    public void setOsversion(String osversion) {
        this.osversion = osversion;
    }

    public String getJavaversion() {
        return javaversion;
    }

    public void setJavaversion(String javaversion) {
        this.javaversion = javaversion;
    }

    public String getProfilename() {
        return profilename;
    }

    public void setProfilename(String profilename) {
        this.profilename = profilename;
    }

    public String getComputername() {
        return computername;
    }

    public void setComputername(String computername) {
        this.computername = computername;
    }

    public String getHarddisk() {
        return harddisk;
    }

    public void setHarddisk(String harddisk) {
        this.harddisk = harddisk;
    }

    public String getMotherboard() {
        return motherboard;
    }

    public void setMotherboard(String motherboard) {
        this.motherboard = motherboard;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (machineid != null ? machineid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MachineBean)) {
            return false;
        }
        MachineBean other = (MachineBean) object;
        if ((this.machineid == null && other.machineid != null) || (this.machineid != null && !this.machineid.equals(other.machineid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jomac.transcription.reference.jpa.models.MachineBean[ machineid=" + machineid + " ]";
    }
}
