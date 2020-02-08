/*
 * JavaChecker.java
 *
 * Created on January 13, 2015, 09:45 AM
 *
 */
package com.hccs.util;

import javax.swing.JOptionPane;

public class JavaChecker {

    private final String OS_NAME = System.getProperty("os.name");
    private final String JAVA_VM_NAME = System.getProperty("java.vm.name");
    private final String JAVA_ARCHITECTURE = System.getenv("PROCESSOR_ARCHITECTURE");
    private final String WOW_64_ARCHITECTURE = System.getenv("PROCESSOR_ARCHITEW6432");

    public boolean validateJavaVersion() {
        return (isOSamd64()) ? JAVA_VM_NAME.contains("64") : !JAVA_VM_NAME.contains("64");
    }

    public String getOSArch() {
        return isOSamd64() ? "amd64" : "x86";
    }

    public boolean isOSamd64() {
        return (JAVA_ARCHITECTURE.contains("64")
                || (WOW_64_ARCHITECTURE != null && WOW_64_ARCHITECTURE.contains("64")));
    }

    public void printInfo() {
        String info = "Info:\n"
                + "OS: " + OS_NAME + "(" + (getOSArch()) + ")\n"
                + "JAVA: " + JAVA_VM_NAME;

        System.out.println(info);
    }

    public static void main(String[] args) {
        JavaChecker jChecker = new JavaChecker();
        jChecker.printInfo();

        if (!jChecker.validateJavaVersion()) {
            JOptionPane.showMessageDialog(null,
                    "Java version is not x64", "Java Checker",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
