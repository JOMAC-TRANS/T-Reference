package com.hccs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComputerInfo {

    public String getHardDiskSN() {
        String result = "";
        String drive = "C"; //Default Drive

        File[] roots = File.listRoots();
        for (int i = 0; i < roots.length; i++) {
            if (roots[i].exists()) {
                if (roots[i].toString().toLowerCase().contains("c")) {
                    drive = "C";
                    break;
                }

                if (roots[i].toString().toLowerCase().contains("d")) {
                    drive = "D";
                    break;
                }
            }
        }

        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                    + "Set colDrives = objFSO.Drives\n"
                    + "Set objDrive = colDrives.item(\"" + drive + "\")\n"
                    + "Wscript.Echo objDrive.SerialNumber";  // see note
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input
                    = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.contains("CScript Error:") ? "" : result.trim();
    }

    public String getMotherBoardSN() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs
                    = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_BaseBoard\") \n"
                    + "For Each objItem in colItems \n"
                    + "    Wscript.Echo objItem.SerialNumber \n"
                    + "    exit for  ' do the first cpu only! \n"
                    + "Next \n";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input
                    = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.contains("CScript Error:") ? "" : result.trim();
    }

    public String getComputerName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "";
        }
    }

    public String getUserName() {
        try {
            return System.getProperty("user.name");
        } catch (Exception e) {
            return "";
        }
    }

    public String getOSInfo() {
        return System.getProperty("os.name") + " "
                + System.getProperty("os.version") + " ("
                + System.getProperty("os.arch") + ")";
    }

    public String getJavaInfo() {
        return "Java " + System.getProperty("java.version")
                + " " + System.getProperty("java.vendor");
    }

    public String getMacAddress() {
        StringBuilder sb = new StringBuilder();
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);

            byte[] mac = null;
            if (ni != null && ((mac = ni.getHardwareAddress()) != null)) {
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
            }
        } catch (Exception e) {
        }

        return sb.toString();
    }

    private String getPublicIP(String wSite) {
        try {
            URL whatismyip = new URL(wSite);
            URLConnection connection = whatismyip.openConnection();
            connection.addRequestProperty("Protocol", "Http/1.1");
            connection.addRequestProperty("Connection", "keep-alive");
            connection.addRequestProperty("Keep-Alive", "1000");
            connection.addRequestProperty("User-Agent", "Web-Agent");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Pattern p = Pattern.compile("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b");
            Matcher matcher = p.matcher(in.readLine());
            while (matcher.find()) {
                return matcher.group();
            }

            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public String getExternalIP() {
        String pubIP = getPublicIP("http://agentgatech.appspot.com/");

        if (pubIP.isEmpty()) {
            System.out.println("WARNING! Check IP Site");
            pubIP = getPublicIP("http://checkip.dyndns.org/");
        }
        return pubIP;
    }

    public static void main(String[] args) {
        ComputerInfo id = new ComputerInfo();

        System.out.println("MB: " + id.getMotherBoardSN());
        System.out.println("HD: " + id.getHardDiskSN());
        System.out.println("C.Name:: " + id.getComputerName());
        System.out.println("MAC: " + id.getMacAddress());
        System.out.println("IP: " + id.getExternalIP());

    }
}