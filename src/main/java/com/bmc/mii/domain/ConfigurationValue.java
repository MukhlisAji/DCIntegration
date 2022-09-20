package com.bmc.mii.domain;

public class ConfigurationValue {

    private String remedyServer;
    private String remedyUsername, remedyPassword;
    private String remedyPort;
    private String remedyMiddleFormCHG;
    private String remedyMiddleFormKontrak;
    private String remedyMiddleFormWOI;
    private String remedyMiddleFormSRM;
    private String documentumCredentils;
    private String documentumURI;
    private String documentumLinkfile;

    public ConfigurationValue(String remedyServer, String remedyUsername, String remedyPassword, String remedyPort, String remedyMiddleFormCHG, String remedyMiddleFormKontrak, String remedyMiddleFormWOI, String remedyMiddleFormSRM, String documentumCredentils, String documentumURI, String documentumLinkfile) {
        this.remedyServer = remedyServer;
        this.remedyUsername = remedyUsername;
        this.remedyPassword = remedyPassword;
        this.remedyPort = remedyPort;
        this.remedyMiddleFormCHG = remedyMiddleFormCHG;
        this.remedyMiddleFormKontrak = remedyMiddleFormKontrak;
        this.remedyMiddleFormWOI = remedyMiddleFormWOI;
        this.remedyMiddleFormSRM = remedyMiddleFormSRM;
        this.documentumCredentils = documentumCredentils;
        this.documentumURI = documentumURI;
        this.documentumLinkfile = documentumLinkfile;
    }

    public String getRemedyServer() {
        return remedyServer;
    }

    public void setRemedyServer(String remedyServer) {
        this.remedyServer = remedyServer;
    }

    public String getRemedyUsername() {
        return remedyUsername;
    }

    public void setRemedyUsername(String remedyUsername) {
        this.remedyUsername = remedyUsername;
    }

    public String getRemedyPassword() {
        return remedyPassword;
    }

    public void setRemedyPassword(String remedyPassword) {
        this.remedyPassword = remedyPassword;
    }

    public String getRemedyPort() {
        return remedyPort;
    }

    public void setRemedyPort(String remedyPort) {
        this.remedyPort = remedyPort;
    }

    public String getRemedyMiddleFormCHG() {
        return remedyMiddleFormCHG;
    }

    public void setRemedyMiddleFormCHG(String remedyMiddleFormCHG) {
        this.remedyMiddleFormCHG = remedyMiddleFormCHG;
    }

    public String getRemedyMiddleFormKontrak() {
        return remedyMiddleFormKontrak;
    }

    public void setRemedyMiddleFormKontrak(String remedyMiddleFormKontrak) {
        this.remedyMiddleFormKontrak = remedyMiddleFormKontrak;
    }

    public String getRemedyMiddleFormWOI() {
        return remedyMiddleFormWOI;
    }

    public void setRemedyMiddleFormWOI(String remedyMiddleFormWOI) {
        this.remedyMiddleFormWOI = remedyMiddleFormWOI;
    }

    public String getRemedyMiddleFormSRM() {
        return remedyMiddleFormSRM;
    }

    public void setRemedyMiddleFormSRM(String remedyMiddleFormSRM) {
        this.remedyMiddleFormSRM = remedyMiddleFormSRM;
    }

    public String getDocumentumCredentils() {
        return documentumCredentils;
    }

    public void setDocumentumCredentils(String documentumCredentils) {
        this.documentumCredentils = documentumCredentils;
    }

    public String getDocumentumURI() {
        return documentumURI;
    }

    public void setDocumentumURI(String documentumURI) {
        this.documentumURI = documentumURI;
    }

    public String getDocumentumLinkfile() {
        return documentumLinkfile;
    }

    public void setDocumentumLinkfile(String documentumLinkfile) {
        this.documentumLinkfile = documentumLinkfile;
    }

}
