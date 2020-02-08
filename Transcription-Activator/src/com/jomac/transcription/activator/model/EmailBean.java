package com.jomac.transcription.activator.model;

public class EmailBean {

    private String fullName, email,
            productName, productVersion;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductNameWdVersion() {
        if (productName != null) {
            if (productVersion != null) {
                return productName.concat(" ").concat(productVersion);
            } else {
                return productName;
            }
        }
        return "";
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }
}
