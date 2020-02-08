package com.jomac.transcription.activator.jpa.models;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "products")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductBean.findAll", query = "SELECT p FROM ProductBean p"),
    @NamedQuery(name = "ProductBean.findByProductid", query = "SELECT p FROM ProductBean p WHERE p.productid = :productid"),
    @NamedQuery(name = "ProductBean.findByName", query = "SELECT p FROM ProductBean p WHERE p.name = :name"),
    @NamedQuery(name = "ProductBean.findByVersion", query = "SELECT p FROM ProductBean p WHERE p.version = :version"),
    @NamedQuery(name = "ProductBean.findByProductschema", query = "SELECT p FROM ProductBean p WHERE p.productschema = :productschema"),
    @NamedQuery(name = "ProductBean.findByProductrequest", query = "SELECT p FROM ProductBean p WHERE p.productrequest = :productrequest"),
    @NamedQuery(name = "ProductBean.findByProductvalue", query = "SELECT p FROM ProductBean p WHERE p.productvalue = :productvalue")})
public class ProductBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "productid")
    private Integer productid;
    @Column(name = "name")
    private String name;
    @Column(name = "version")
    private String version;
    @Column(name = "productschema")
    private String productschema;
    @Column(name = "productrequest")
    private String productrequest;
    @Column(name = "productvalue")
    private String productvalue;

    public ProductBean() {
    }

    public ProductBean(Integer productid) {
        this.productid = productid;
    }

    public Integer getProductid() {
        return productid;
    }

    public void setProductid(Integer productid) {
        this.productid = productid;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version != null ? version : "";
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProductschema() {
        return productschema != null ? productschema : "";
    }

    public void setProductschema(String productschema) {
        this.productschema = productschema;
    }

    public String getProductrequest() {
        return productrequest != null ? productrequest : "";
    }

    public void setProductrequest(String productrequest) {
        this.productrequest = productrequest;
    }

    public String getProductvalue() {
        return productvalue != null ? productvalue : "";
    }

    public void setProductvalue(String productvalue) {
        this.productvalue = productvalue;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productid != null ? productid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductBean)) {
            return false;
        }
        ProductBean other = (ProductBean) object;
        if ((this.productid == null && other.productid != null) || (this.productid != null && !this.productid.equals(other.productid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getName() + " " + getVersion();
    }
}
