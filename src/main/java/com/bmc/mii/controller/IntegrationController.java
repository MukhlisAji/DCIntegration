/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.mii.controller;

import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.EntryListInfo;
//import static com.bmc.mii.controller.ConnectionTesting.logger;
import static com.bmc.mii.controller.remedyController.logger;
import com.bmc.mii.documentum.DocumentumDomain;
import com.bmc.mii.documentum.DocumentumRest;
import com.bmc.mii.domain.ConfigurationValue;
import com.bmc.mii.domain.DocumentumFile;
import com.bmc.mii.domain.RemedyAPI;
import com.bmc.mii.domain.RemedyAttachment;
import com.bmc.mii.remedy.FileMetadata;
import com.bmc.mii.remedy.RemedyConnection;
import com.sun.security.auth.login.ConfigFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author MukhlisAj
 */
@Controller
public class IntegrationController {

    /*
     * ini contoh buat attachment smart... 
     */
    @RequestMapping(value = "getFilename", method = RequestMethod.GET)
    public String getFilename(Model model) {
        //Get configuration value from DCconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(com.bmc.mii.domain.ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //Connection testing
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);

        RemedyAPI remedyAPI = new RemedyAPI();

//        String ticket = "000000000000001";
        //get attachment 
//        String instanceID = "0";
//        String form = "'Integration Status' =\"0\"";
//        logger.info("form " + form);
        ArrayList<RemedyAttachment> arrayofAttachment = new ArrayList<RemedyAttachment>();
//        arrayofAttachment = remedyAPI.getRemedyAttachmentbySchemaQuery(remedyServer, configValue.getRemedyMiddleFormKontrak(), "'Status'=\"0\"");
        List<EntryListInfo> eListInfos = remedyAPI.getRemedyRecordByQuery(remedyServer, configValue.getRemedyMiddleFormKontrak(), "'Status'=\"0\"");

        logger.info("array " + eListInfos);
//        logger.info("array 2 " + arrayofAttachment);
        System.out.println(arrayofAttachment);
//        ResponseEntity<String> response = null;
        String trueName = "";
        for (RemedyAttachment remedyAttachment : arrayofAttachment) {
            String Filename = remedyAttachment.getFilename();
            trueName = Filename.substring(0, Filename.indexOf("."));
            logger.info("Nama file = " + trueName);
//            logger.info("Nama file = " + response.getBody());

        }
        model.addAttribute("result", "base 64 is : " + trueName);
        return "result";
    }


    @RequestMapping(value = "tesSent", method = RequestMethod.GET)
    public List<String> sendAttachment() throws IOException {

        //Get configuration value from DCconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(com.bmc.mii.domain.ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);        //get attachment 
//        ConfigurationValue configValue = context.get
        String requestID = "000000000000416";

        //Connection testing
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        ArrayList<RemedyAttachment> arrayofAttachment = new ArrayList<RemedyAttachment>();
        arrayofAttachment = remedyAPI.getRemedyAttachmentbySchemaQuery(remedyServer,
                "MDR:Kontrak",
                "'Request ID__c' LIKE \"" + requestID + "\"");
//        System.out.println(arrayofAttachment);
        ResponseEntity<String> response = null;
        List<String> linkDocumentum = new ArrayList<>();
        String link = "";
        for (RemedyAttachment remedyAttachment : arrayofAttachment) {
            String fileName = remedyAttachment.getFilename();
            String trueName = fileName.substring(0, fileName.indexOf("."));
            DocumentumFile documentumFile = new DocumentumFile();
            documentumFile.setExtension(FilenameUtils.getExtension(remedyAttachment.getFilename()));

            String fileExtension = FilenameUtils.getExtension(remedyAttachment.getFilename());
            logger.info("File Extension  = " + fileExtension);
            logger.info("===================================================================================================");
            RestTemplate rt = new RestTemplate();
            rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            rt.getMessageConverters().add(new StringHttpMessageConverter());
            String newFileExtension = DocumentumRest.getFormat(fileExtension);
            String uri = "http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/folders/0c035bd680054669/documents?format=" + newFileExtension;
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
            remedyController rc = new remedyController();
//            if (Schema.contains("Kontrak")) {
//                isKontrak = true;
            link = rc.getLinkDocumentum(isKontrak, response.getBody());
//            } else {
//                isKontrak = false;
//                link = getLinkDocumentum(isKontrak, response.getBody());
//
//            }

            linkDocumentum.add(link);

            logger.info("send to Documentum body : " + response.getBody());
            logger.info("Documentum link : " + linkDocumentum);
        }
        return linkDocumentum;

    }

}
