/*
 * FileDragDropCallback.java
 *
 * Created on September 18, 2010, 3:19:23 PM
 */
package com.hccs.forms.components;

import java.io.File;

public interface FileDragDropCallback {

    /**
     * Validates selected files.
     *
     * @param files
     * @return boolean
     */
    public boolean validate(File[] files);

    /**
     * Define what to do with the files
     *
     * @param files
     */
    public void processFiles(File[] files);
}
