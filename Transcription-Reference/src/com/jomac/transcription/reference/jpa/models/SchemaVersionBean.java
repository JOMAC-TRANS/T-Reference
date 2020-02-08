package com.jomac.transcription.reference.jpa.models;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "SCHEMA_VERSION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SchemaVersionBean.findAll", query = "SELECT s FROM SchemaVersionBean s"),
    @NamedQuery(name = "SchemaVersionBean.findByVersion", query = "SELECT s FROM SchemaVersionBean s WHERE s.version = :version")})
public class SchemaVersionBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "VERSION")
    private String version;
    @Column(name = "DBNAME")
    private String dbname;

    public SchemaVersionBean() {
    }

    public SchemaVersionBean(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (version != null ? version.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SchemaVersionBean)) {
            return false;
        }
        SchemaVersionBean other = (SchemaVersionBean) object;
        if ((this.version == null && other.version != null) || (this.version != null && !this.version.equals(other.version))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jomac.transcription.reference.resources.jpa.models.SchemaVersionBean[ version=" + version + " ]";
    }
}
