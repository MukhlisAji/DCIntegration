/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.mii.controller;

import java.util.ArrayList;
import org.apache.log4j.Logger;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.bmc.mii.documentum.DocumentumDomain;
import com.bmc.mii.documentum.DocumentumRest;
import com.bmc.mii.domain.ConfigurationValue;
import com.bmc.mii.domain.DocumentumFile;
import com.bmc.mii.domain.RemedyAPI;
import com.bmc.mii.domain.RemedyAttachment;
import com.bmc.mii.remedy.FileMetadata;
import com.bmc.mii.remedy.RemedyConnection;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Array;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author MukhlisAj
 */
public class remedyController {

    protected static Logger logger = Logger.getLogger("RemedyConnection: ");

    public List<String> sendAttachment(
            FileMetadata fileMetadata,
            RemedyAPI remedyAPI,
            ARServerUser remedyServer,
            String Schema, String query) throws IOException {

        //Get configuration value from DCconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(com.bmc.mii.domain.ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
        //get attachment 
//      ConfigurationValue configValue = context.get
        ArrayList<RemedyAttachment> arrayofAttachment = new ArrayList<RemedyAttachment>();
        arrayofAttachment = remedyAPI.getRemedyAttachmentbySchemaQuery(remedyServer,
                Schema,
                query);
//        System.out.println(arrayofAttachment);
        ResponseEntity<String> response = null;
        List<String> linkDocumentum = new ArrayList<>();
        String link = "";
        try {

            for (RemedyAttachment remedyAttachment : arrayofAttachment) {
                String fileName = remedyAttachment.getFilename();
                String trueName = fileName.substring(0, fileName.indexOf("."));
                DocumentumFile documentumFile = new DocumentumFile();
                documentumFile.setExtension(FilenameUtils.getExtension(remedyAttachment.getFilename()));

                try {

                    String fileExtension = FilenameUtils.getExtension(remedyAttachment.getFilename());
                    logger.info("File Extension  = " + fileExtension);
                    logger.info("===================================================================================================");
                    RestTemplate rt = new RestTemplate();
                    rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    rt.getMessageConverters().add(new StringHttpMessageConverter());
                    String newFileExtension = DocumentumRest.getFormat(fileExtension);
//                String URI = configValue.getDocumentumURI();
                    String uri = configValue.getDocumentumURI() + newFileExtension;
                    logger.info("the url is : " + uri);

//            String plainCreds = "admin_bmc:P@ssw0rd.1";
                    String Credentials = configValue.getDocumentumCredentils();
                    byte[] plainCredsBytes = Credentials.getBytes();
                    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
                    String base64Creds = new String(base64CredsBytes);

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Authorization", "Basic " + base64Creds);

                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                    DocumentumDomain documentumDomain = new DocumentumDomain();
                    documentumDomain.appr_object_type = fileExtension;
                    documentumDomain.object_name = remedyAttachment.getFilename();
                    documentumDomain.File = remedyAttachment.getContent64().getBytes();

                    MultiValueMap<String, Object> body
                            = new LinkedMultiValueMap<>();
                    body.add("properties", "{\n"
                            + "\"properties\":\n"
                            + " {\n"
                            + "\"r_object_type\":\"dm_document\",\n"
                            + "\"object_name\":\"" + trueName + "\"\n"
                            + "}\n"
                            + "}");

                    body.add("content", remedyAttachment.getAttachedFile());
                    HttpEntity<MultiValueMap<String, Object>> requestEntity
                            = new HttpEntity<>(body, headers);
                    response = rt.exchange(uri, HttpMethod.POST, requestEntity, String.class);
                    response.getBody();
                    boolean isKontrak = false;
                    if (Schema.contains("Kontrak")) {
                        isKontrak = true;
                        link = getLinkDocumentum(isKontrak, response.getBody());
                    } else {
                        isKontrak = false;
                        link = getLinkDocumentum(isKontrak, response.getBody());

                    }

                    linkDocumentum.add(link);

                } catch (Exception e) {
                    logger.info("error sending attachments to dokumentum :" + e);
                }

                logger.info("send to Documentum body : " + response.getBody());
                logger.info("Documentum link : " + linkDocumentum);
            }

        } catch (Exception e) {
            logger.info("error sending attachments to dokumentum :" + e);
        }
        return linkDocumentum;
    }

    public void getRemedyTicketKontrak() throws IOException {
        //Get configuration value from DCconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(com.bmc.mii.domain.ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //Connection testing
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);
        String Schema = configValue.getRemedyMiddleFormKontrak();
        RemedyAPI remedyAPI = new RemedyAPI();
        logger.info("{{{{{{{{{{{{{{{{{{{{{Processing from MDR:Kontrak }}}}}}}}}}}}}}}}}}}}}");
        //temporary variable
        Integer statusEntryId = 0, detailedDescription = 0, detailedDescription2 = 0, detailedDescription3 = 0,
                detailedDescription4 = 0, detailedDescription5 = 0, detailedDescription6 = 0, detailedDescription7 = 0, detailedDescription8 = 0;
        FileMetadata fileMetadata = new FileMetadata();
        String detileDesc = "", requestID = "", nomerPO = "", nomerSPK = "";
        AttachmentValue Attachment1 = null, Attachment2 = null, Attachment3 = null, Attachment4 = null, Attachment5 = null,
                Attachment6 = null, Attachment7 = null, Attachment8 = null;
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedyServer, configValue.getRemedyMiddleFormKontrak(), "'Status'=\"0\"");

        try {
            for (EntryListInfo eListInfo : eListInfos) {
                Entry record = remedyServer.getEntry(Schema, eListInfo.getEntryID(), null);
                for (Integer i : record.keySet()) {
                    Field field = remedyServer.getField(Schema, i);
                    Value val = record.get(i);
                    if (val.getValue() != null) {
                        switch (field.getName()) {
                            case "Request ID__c":
                                fileMetadata.setBmcTicketNumber(val.getValue().toString());
                                requestID = val.getValue().toString();
                                break;
                            case "Nomor SPK":
                                fileMetadata.setNoSPK(val.getValue().toString());
                                nomerSPK = val.getValue().toString();
                                break;
                            case "Nomor BAST":
                                fileMetadata.setNoBAST(val.getValue().toString());
                                nomerSPK = val.getValue().toString();
                                break;
                            case "Nomor Kontrak":
                                fileMetadata.setNoKontrak(val.getValue().toString());
                                nomerSPK = val.getValue().toString();
                                break;
                            case "Attachment Kontrak__c":
//                                fileMetadata.setNoKontrak(val.getValue().toString());
                                Attachment1 = (AttachmentValue) val.getValue();
                                break;
                            case "Attachment SPK__c":
//                                fileMetadata.setNoKontrak(val.getValue().toString());
                                Attachment2 = (AttachmentValue) val.getValue();
                                break;
                            case "Attachment BAST__c":
//                                fileMetadata.setNoKontrak(val.getValue().toString());
                                Attachment3 = (AttachmentValue) val.getValue();
                                break;
                            case "Attachment PO 1__c":
//                                fileMetadata.setNoKontrak(val.getValue().toString());
                                Attachment4 = (AttachmentValue) val.getValue();
                                break;
                            case "Attachment PO 2__c":
//                                fileMetadata.setNoKontrak(val.getValue().toString());
                                Attachment5 = (AttachmentValue) val.getValue();
                                break;
                            case "Attachment PO 3__c":
//                                fileMetadata.setNoKontrak(val.getValue().toString());
                                Attachment6 = (AttachmentValue) val.getValue();
                                break;
                            case "Attachment PO 4__c":
//                                fileMetadata.setNoKontrak(val.getValue().toString());
                                Attachment7 = (AttachmentValue) val.getValue();
                                break;
                            case "Attachment PO 5__c":
//                                fileMetadata.setNoKontrak(val.getValue().toString());
                                Attachment8 = (AttachmentValue) val.getValue();
                                break;
                            case "Nomor PO":
                                fileMetadata.setNoPO(val.getValue().toString());
                                nomerPO = val.getValue().toString();
                                break;
                            case "Link Dokumen Kontrak":
//                                fileMetadata.setDetailedDescription(val.getValue().toString());
                                detailedDescription = i;
//                                detileDesc = val.getValue().toString();
                                break;
                            case "Link Dokumen SPK":
//                                fileMetadata.setDetailedDescription(val.getValue().toString());
                                detailedDescription2 = i;
//                                detileDesc = val.getValue().toString();
                                break;
                            case "Link Dokumen BAST":
//                                fileMetadata.setDetailedDescription(val.getValue().toString());
                                detailedDescription3 = i;
//                                detileDesc = val.getValue().toString();
                                break;
                            case "Link Dokumen PO 1":
//                                fileMetadata.setDetailedDescription(val.getValue().toString());
                                detailedDescription4 = i;
//                                detileDesc = val.getValue().toString();
                                break;
                            case "Link Dokumen PO 2":
//                                fileMetadata.setDetailedDescription(val.getValue().toString());
                                detailedDescription5 = i;
//                                detileDesc = val.getValue().toString();
                                break;
                            case "Link Dokumen PO 3":
//                                fileMetadata.setDetailedDescription(val.getValue().toString());
                                detailedDescription6 = i;
//                                detileDesc = val.getValue().toString();
                                break;
                            case "Link Dokumen PO 4":
//                                fileMetadata.setDetailedDescription(val.getValue().toString());
                                detailedDescription7 = i;
//                                detileDesc = val.getValue().toString();
                                break;
                            case "Link Dokumen PO 5":
//                                fileMetadata.setDetailedDescription(val.getValue().toString());
                                detailedDescription8 = i;
//                                detileDesc = val.getValue().toString();
                                break;
                            case "Status":
                                statusEntryId = i;
                                break;
                        }
                        logger.info(field.getName() + ":" + val.getValue());

                    }

                }
                record.put(statusEntryId, new Value("2"));
                remedyServer.setEntry(Schema, record.getEntryId(), record, null, 0);
                logger.info("------- value ------");

//                logger.info("Attachment1 = " + Attachment1.getValueFileName());
//                for (RemedyAttachment remedyAttachment : Attachment1) {
//                    
//                }
                String query = "'Request ID__c' LIKE \"" + requestID + "\"";
                List<String> documentumLink = sendAttachment(fileMetadata, remedyAPI, remedyServer, Schema, query);
//                String[] documentumLink = {"http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/objects/09035bd68005e8b4/content-media?format=jpeg&modifier&page=0", "http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/objects/09035bd68005e8b5/content-media?format=png&modifier&page=0", "http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/objects/09035bd68005e8b6/content-media?format=jpeg&modifier&page=0"};

//                logger.info(documentumLink);
//
                if (documentumLink.isEmpty()) {
                    record.put(statusEntryId, new Value("2"));
//
                } else {
                    for (String Link : documentumLink) {
                        String newlink = Link.substring(0, Link.indexOf("|"));
                        String Objectname = Link.substring(Link.indexOf("|") + 1);
                        logger.info("link = " + newlink);
                        logger.info("link = " + Objectname);
//                        logger.info("link = " + Attachment1.getValueFileName());
                        if (Attachment1 != null) {
                            if (Attachment1.getValueFileName().contains(Objectname)) {
                                record.put(detailedDescription, new Value(newlink + "\n\n"));
                            }
                        }

                        if (Attachment2 != null) {
                            if (Attachment2.getValueFileName().contains(Objectname)) {
                                record.put(detailedDescription2, new Value(newlink + "\n\n"));
                            }

                        }

                        if (Attachment3 != null) {
                            if (Attachment3.getValueFileName().contains(Objectname)) {
                                record.put(detailedDescription3, new Value(newlink + "\n\n"));
                            }
                        }

                        if (Attachment4 != null) {
                            if (Attachment4.getValueFileName().contains(Objectname)) {
                                record.put(detailedDescription4, new Value(newlink + "\n\n"));
                            }
                        }
                        if (Attachment5 != null) {
                            if (Attachment5.getValueFileName().contains(Objectname)) {
                                record.put(detailedDescription5, new Value(newlink + "\n\n"));
                            }
                        }
                        if (Attachment6 != null) {
                            if (Attachment6.getValueFileName().contains(Objectname)) {
                                record.put(detailedDescription6, new Value(newlink + "\n\n"));
                            }
                        }
                        if (Attachment7 != null) {
                            if (Attachment7.getValueFileName().contains(Objectname)) {
                                record.put(detailedDescription7, new Value(newlink + "\n\n"));
                            }
                        }
                        if (Attachment8 != null) {
                            if (Attachment8.getValueFileName().contains(Objectname)) {
                                record.put(detailedDescription8, new Value(newlink + "\n\n"));
                            }
                        }

                    }
                    record.put(statusEntryId, new Value("1"));
                }
                //Updating SRT middle form based on response from SAP
                remedyServer.setEntry(Schema, record.getEntryId(), record, null, 0);
            }

        } catch (ARException e) {
            logger.info("ARException Error on sendMetadata: " + e.toString());
        }

        //return fileMetadata;
        //Closing ApplicationContext to avoid memory leak
        ((AbstractApplicationContext) context).close();

    }

    public void getRemedyTicketWOI() throws IOException {
        //Get configuration value from DCconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(com.bmc.mii.domain.ConfigFile.class
        );
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class
        );

        //Connection testing
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);
        String Schema = configValue.getRemedyMiddleFormWOI();
        RemedyAPI remedyAPI = new RemedyAPI();
        logger.info("{{{{{{{{{{{{{{{{{{{{{Processing from WOI:WorkInfo }}}}}}}}}}}}}}}}}}}}}");
        //temporary variable
        Integer statusEntryId = 0, detailedDescription = 0;
        FileMetadata fileMetadata = new FileMetadata();
        String detileDesc = "", instanceID = "";

        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedyServer, configValue.getRemedyMiddleFormWOI(), "'Status Attachment'=\"0\"");

        try {
            for (EntryListInfo eListInfo : eListInfos) {
                Entry record = remedyServer.getEntry(Schema, eListInfo.getEntryID(), null);
                for (Integer i : record.keySet()) {
                    Field field = remedyServer.getField(Schema, i);
                    Value val = record.get(i);
                    if (val.getValue() != null) {
                        switch (field.getName()) {
                            case "InstanceId":
                                fileMetadata.setInstanceId(val.getValue().toString());
                                instanceID = val.getValue().toString();
                                break;
                            case "Detailed Description":
                                fileMetadata.setDetailedDescription(val.getValue().toString());
                                detailedDescription = i;
                                detileDesc = val.getValue().toString() + "\n";
                                break;
                            case "Status Attachment":
                                statusEntryId = i;
                                break;
                        }
                        logger.info(field.getName() + ":" + val.getValue());

                    }

                }
                record.put(statusEntryId, new Value("2"));
                remedyServer.setEntry(Schema, record.getEntryId(), record, null, 0);
                logger.info("------- value ------");
                String query = "'InstanceId' LIKE \"" + instanceID + "\"";
                List<String> documentumLink = sendAttachment(fileMetadata, remedyAPI, remedyServer, Schema, query);
//                {"http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/objects/09035bd68005e8b4/content-media?format=jpeg&modifier&page=0", "http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/objects/09035bd68005e8b5/content-media?format=png&modifier&page=0", "http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/objects/09035bd68005e8b6/content-media?format=jpeg&modifier&page=0"};

                logger.info(documentumLink);

                if (documentumLink.isEmpty()) {
                    record.put(statusEntryId, new Value("2"));

                } else {
                    record.put(statusEntryId, new Value("1"));
                    for (String Link : documentumLink) {
                        detileDesc += "\n" + "Link documentum is : " + Link;
                        record.put(detailedDescription, new Value(detileDesc));

                    }
                }

                //Updating SRT middle form based on response from SAP
                remedyServer.setEntry(Schema, record.getEntryId(), record, null, 0);
            }

        } catch (ARException e) {
            logger.info("ARException Error on sendMetadata: " + e.toString());
        }

        //return fileMetadata;
        //Closing ApplicationContext to avoid memory leak
        ((AbstractApplicationContext) context).close();

    }

    public void getRemedyTicketSRM() throws IOException {
        //Get configuration value from DCconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(com.bmc.mii.domain.ConfigFile.class
        );
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class
        );

        //Connection testing
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);
        String Schema = configValue.getRemedyMiddleFormSRM();
        RemedyAPI remedyAPI = new RemedyAPI();
        logger.info("{{{{{{{{{{{{{{{{{{{{{Processing from SRM:WorkInfo }}}}}}}}}}}}}}}}}}}}}");
        //temporary variable
        Integer statusEntryId = 0, detailedDescription = 0;
        FileMetadata fileMetadata = new FileMetadata();
        String detileDesc = "", instanceID = "";

        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedyServer, configValue.getRemedyMiddleFormSRM(), "'Status Attachment'=\"0\"");

        try {
            for (EntryListInfo eListInfo : eListInfos) {
                Entry record = remedyServer.getEntry(Schema, eListInfo.getEntryID(), null);
                for (Integer i : record.keySet()) {
                    Field field = remedyServer.getField(Schema, i);
                    Value val = record.get(i);
                    if (val.getValue() != null) {
                        switch (field.getName()) {
                            case "InstanceId":
                                fileMetadata.setInstanceId(val.getValue().toString());
                                instanceID = val.getValue().toString();
                                break;
                            case "Notes":
                                fileMetadata.setDetailedDescription(val.getValue().toString());
                                detailedDescription = i;
                                detileDesc = val.getValue().toString() + "\n";
                                break;
                            case "Status Attachment":
                                statusEntryId = i;
                                break;
                        }
                        logger.info(field.getName() + ":" + val.getValue());

                    }

                }
                record.put(statusEntryId, new Value("2"));
                logger.info("------- value ------");
                String query = "'InstanceId' LIKE \"" + instanceID + "\"";
                List<String> documentumLink = sendAttachment(fileMetadata, remedyAPI, remedyServer, Schema, query);
//                {"http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/objects/09035bd68005e8b4/content-media?format=jpeg&modifier&page=0", "http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/objects/09035bd68005e8b5/content-media?format=png&modifier&page=0", "http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/objects/09035bd68005e8b6/content-media?format=jpeg&modifier&page=0"};

                logger.info(documentumLink);

                if (documentumLink.isEmpty()) {
                    record.put(statusEntryId, new Value("2"));

                } else {
                    record.put(statusEntryId, new Value("1"));
                    for (String Link : documentumLink) {
                        detileDesc += "\n" + "Link documentum is : " + Link;
                        record.put(detailedDescription, new Value(detileDesc));

                    }
                }

                //Updating SRT middle form based on response from SAP
                remedyServer.setEntry(Schema, record.getEntryId(), record, null, 0);
            }

        } catch (ARException e) {
            logger.info("ARException Error on sendMetadata: " + e.toString());
        }

        //return fileMetadata;
        //Closing ApplicationContext to avoid memory leak
        ((AbstractApplicationContext) context).close();

    }

    public void getRemedyTicketWorkLog() throws IOException {
        //Get configuration value from DCconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(com.bmc.mii.domain.ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //Connection testing
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);
        String Schema = configValue.getRemedyMiddleFormCHG();
        RemedyAPI remedyAPI = new RemedyAPI();
        logger.info("{{{{{{{{{{{{{{{{{{{{{Processing from CHG:WorkLog }}}}}}}}}}}}}}}}}}}}}");
        //temporary variable
        Integer statusEntryId = 0, detailedDescription = 0;
        FileMetadata fileMetadata = new FileMetadata();
        String detileDesc = "", instanceID = "";

        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedyServer, configValue.getRemedyMiddleFormCHG(), "'Integration Status'=\"0\"");

        try {
            for (EntryListInfo eListInfo : eListInfos) {
                Entry record = remedyServer.getEntry(Schema, eListInfo.getEntryID(), null);
                for (Integer i : record.keySet()) {
                    Field field = remedyServer.getField(Schema, i);
                    Value val = record.get(i);
                    if (val.getValue() != null) {
                        switch (field.getName()) {
                            case "Infrastructure Change ID":
                                fileMetadata.setInfrastructureID(val.getValue().toString());
                                break;
                            case "InstanceId":
                                instanceID = val.getValue().toString();
                                break;
                            case "Detailed Description":
                                fileMetadata.setDetailedDescription(val.getValue().toString());
                                detailedDescription = i;
                                detileDesc = val.getValue().toString() + "\n";
                                break;
                            case "Integration Status":
                                statusEntryId = i;
                                break;
                        }
                        logger.info(field.getName() + ":" + val.getValue());

                    }

                }

                try {
                    record.put(statusEntryId, new Value("2"));
                    logger.info("------- value ------");

                    String query = "'InstanceId' LIKE \"" + instanceID + "\"";

                    List<String> documentumLink = sendAttachment(fileMetadata, remedyAPI, remedyServer, Schema, query);

                    if (documentumLink.isEmpty()) {
                        record.put(statusEntryId, new Value("2"));

                    } else {
                        record.put(statusEntryId, new Value("1"));
                        for (String Link : documentumLink) {
                            detileDesc += "\n" + "Link documentum is : " + Link;
                            record.put(detailedDescription, new Value(detileDesc));

                        }
                    }
                    remedyServer.setEntry(Schema, record.getEntryId(), record, null, 0);

                } catch (Exception e) {
                    record.put(statusEntryId, new Value("2"));
                }
                //                {"http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/objects/09035bd68005e8b4/content-media?format=jpeg&modifier&page=0", "http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/objects/09035bd68005e8b5/content-media?format=png&modifier&page=0", "http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/objects/09035bd68005e8b6/content-media?format=jpeg&modifier&page=0"};

//                logger.info(query + "----------" + documentumLink);
                //Updating SRT middle form based on response from SAP
            }

        } catch (ARException e) {
            logger.info("ARException Error on sendMetadata: " + e.toString());
        }

        //return fileMetadata;
        //Closing ApplicationContext to avoid memory leak
        ((AbstractApplicationContext) context).close();

    }

    public String getLinkDocumentum(boolean isKontrak, String jsonData) throws IOException {
        //Get configuration value from DCconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(com.bmc.mii.domain.ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory factory = objectMapper.getFactory();
        JsonParser parser = factory.createParser(jsonData);
        //read JSON
        JsonNode rootNode = objectMapper.readTree(parser);
        JsonNode objectID = rootNode.get("properties").get("r_object_id");
        JsonNode contentType = rootNode.get("properties").get("a_content_type");
        JsonNode nameFile = rootNode.get("properties").get("object_name");

        logger.info("object ID = " + objectID.toString());
        logger.info("object ID = " + contentType.toString());
        logger.info("object ID = " + nameFile.toString());

        String urlViewer = configValue.getDocumentumLinkfile() + objectID.asText() + "/content-media?format=" + contentType.textValue() + "&modifier&page=0";

        if (isKontrak == true) {
            return urlViewer + "|" + nameFile.textValue();
        } else {
            return urlViewer;
        }

    }

}
