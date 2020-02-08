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
    @GeneratedValue(generator = "product_sequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "product_sequence", sequenceName = "product_sequence", allocationSize = 1)
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productid")
    private List<RegistrationBean> registrationBeanList;

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
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProductschema() {
        return productschema;
    }

    public void setProductschema(String productschema) {
        this.productschema = productschema;
    }

    public String getProductrequest() {
        return productrequest;
    }

    public void setProductrequest(String productrequest) {
        this.productrequest = productrequest;
    }

    public String getProductvalue() {
        return productvalue;
    }

    public void setProductvalue(String productvalue) {
        this.productvalue = productvalue;
    }

    @XmlTransient
    public List<RegistrationBean> getRegistrationBeanList() {
        return registrationBeanList;
    }

    public void setRegistrationBeanList(List<RegistrationBean> registrationBeanList) {
        this.registrationBeanList = registrationBeanList;
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
        return "com.jomac.transcription.reference.jpa.models.ProductBean[ productid=" + productid + " ]";
    }
}
