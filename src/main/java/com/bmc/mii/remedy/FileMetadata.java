/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.mii.remedy;

/**
 *
 * @author MukhlisAj
 */
public class FileMetadata {

    private String InfrastructureID,
            objectStore,
            modul,
            bmcTicketNumber,
            DetailedDescription,
            InstanceId,
            noPO,
            noBAST,
            noKontrak,
            noSPK;

    public String getNoBAST() {
        return noBAST;
    }

    public void setNoBAST(String noBAST) {
        this.noBAST = noBAST;
    }

    public String getNoKontrak() {
        return noKontrak;
    }

    public void setNoKontrak(String noKontrak) {
        this.noKontrak = noKontrak;
    }

    public String getNoPO() {
        return noPO;
    }

    public void setNoPO(String noPO) {
        this.noPO = noPO;
    }

    public String getNoSPK() {
        return noSPK;
    }

    public void setNoSPK(String noSPK) {
        this.noSPK = noSPK;
    }

    public String getInstanceId() {
        return InstanceId;
    }

    public void setInstanceId(String InstanceId) {
        this.InstanceId = InstanceId;
    }

    public String getDetailedDescription() {
        return DetailedDescription;
    }

    public void setDetailedDescription(String DetailedDescription) {
        this.DetailedDescription = DetailedDescription;
    }

    public String getObjectStore() {
        return objectStore;
    }

    public String getInfrastructureID() {
        return InfrastructureID;
    }

    public void setInfrastructureID(String InfrastructureID) {
        this.InfrastructureID = InfrastructureID;
    }

    public void setObjectStore(String objectStore) {
        this.objectStore = objectStore;
    }

    public String getModul() {
        return modul;
    }

    public void setModul(String modul) {
        this.modul = modul;
    }

    public String getBmcTicketNumber() {
        return bmcTicketNumber;
    }

    public void setBmcTicketNumber(String bmcTicketNumber) {
        this.bmcTicketNumber = bmcTicketNumber;
    }

}
