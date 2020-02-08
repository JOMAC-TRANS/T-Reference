package com.jomac.transcription.activator.utility;

import com.csvreader.CsvReader;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class IpAddressChecker {

    private final String filePath = "/com/jomac/transcription/activator/resources/ph.csv";
    private String ipAddress;
    private Object[] data = null;

    public IpAddressChecker() {
        data = loadCSVFile();
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    private Object[] loadCSVFile() {
        CsvReader rd;
        InputStream is = null;
        List<Object> _data = new ArrayList<>();
        try {
            is = IpAddressChecker.class.getResourceAsStream(filePath);
            rd = new CsvReader(is, Charset.defaultCharset());
            rd.readHeaders();
            Object[] columns = rd.getHeaders();
            _data.add(columns);
            while (rd.readRecord()) {
                Object[] rows = rd.getValues();
                _data.add(rows);
            }
            rd.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }
        }
        return _data.toArray();
    }

    public boolean isInRange() {
        boolean inRange = false;
        for (Object data1 : data) {
            Object[] kards = (Object[]) data1;
            try {
                long ipLo = ipToLong(InetAddress.getByName((String) kards[0]));
                long ipHi = ipToLong(InetAddress.getByName((String) kards[1]));
                long ipToTest = ipToLong(InetAddress.getByName(ipAddress));

                if (ipToTest >= ipLo && ipToTest <= ipHi) {
                    inRange = true;
                    break;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return false;
            }
        }
        return inRange;
    }

    private long ipToLong(InetAddress ip) {
        byte[] octets = ip.getAddress();
        long result = 0;
        for (byte octet : octets) {
            result <<= 8;
            result |= octet & 0xff;
        }
        return result;
    }

    public boolean isValidIPFormat() {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return false;
        }

        String ip_address_pat = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        return Pattern.compile(ip_address_pat)
                .matcher(ipAddress).matches();
    }

    public static void main(String[] args) {
        IpAddressChecker ipChecker = new IpAddressChecker();
        ipChecker.setIpAddress("122.52.104.128");
        if (ipChecker.isValidIPFormat()) {
            if (ipChecker.isInRange()) {
                System.out.println("Your ip is block in the philippines");
            } else {
                System.out.println("Your ip is NOT block in the philippines");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid IP Address");
        }
    }
}
