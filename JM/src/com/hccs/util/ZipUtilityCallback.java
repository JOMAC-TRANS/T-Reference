/*
 * ZipUtilityCallback.java
 *
 * Created on September 21, 2010, 10:34 PM
 *
 */
package com.hccs.util;

public interface ZipUtilityCallback {

    public String getZipPassword(String fileName);

    public void showInvalidPassword(String fileName);

    public void showErrorMessage(String fileName);
}
