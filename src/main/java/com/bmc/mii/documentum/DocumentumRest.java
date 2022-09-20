/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.mii.documentum;

import com.bmc.mii.domain.DocumentumFile;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
public class DocumentumRest {

    /*
     * This for creating session in dcumentum
     */
    public static void SetRest() {

        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        rt.getMessageConverters().add(new StringHttpMessageConverter());
        String uri = "http://10.243.132.237:8080/dctm-rest/repositories/ECM_IT/folders/0c035bd680054669/documents?format=pdf";

        String plainCreds = "admin_bmc:P@ssw0rd.1";
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        DocumentumDomain documentumDomain = new DocumentumDomain();
        documentumDomain.appr_object_type = "";
        documentumDomain.object_name = "";
        String file = "";
        documentumDomain.File = file.getBytes();

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("File", file);
        body.add("properties", documentumDomain);
//        body.add("object_name", file);
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = rt.exchange(uri, HttpMethod.POST, requestEntity, String.class);
        response.getBody();
    }

    /**
     * This method is for checking the extension file
     *
     * @param completeFileName
     * @return
     * @throws com.documentum.fc.common.DfException
     */
    public static String getFormat(String extension) {
        DocumentumFile documentumFile = new DocumentumFile();
        HashMap<String, String> documentumFileFormat = (HashMap<String, String>) fetchCommonDocumentumFileFormats();
        String format = "unknown";
//        String extension = documentumFile.getExtension();
//        String extension = "jpg";
        if (documentumFileFormat.containsKey(extension)) {
            format = documentumFileFormat.get(extension);
        } else {
           return "";
        }
//        log("Documentum file format is " + format, null, null);
        return format;
    }
        
        public static Map<String, String> fetchCommonDocumentumFileFormats() {
//        log("Loading documentum file formats...", null, null);
        Map<String, String> documentumFileFormat = new HashMap<String, String>();
        documentumFileFormat = new HashMap<String, String>();
        documentumFileFormat.put("txt", "crtext");
        documentumFileFormat.put("doc", "msw8");
        documentumFileFormat.put("docx", "msw12");
        documentumFileFormat.put("xls", "excel");
        documentumFileFormat.put("xlsx", "excel12book");
        documentumFileFormat.put("ppt", "powerpoint");
        documentumFileFormat.put("pptx", "ppt12");
        documentumFileFormat.put("pdf", "pdf");
        documentumFileFormat.put("png", "png");
        documentumFileFormat.put("jpg", "jpeg");
        documentumFileFormat.put("tif", "tiff");
        documentumFileFormat.put("bmp", "bmp");
        documentumFileFormat.put("gif", "gif");
        documentumFileFormat.put("zip", "zip");
        documentumFileFormat.put("xlsm", "excel12mebook");
//        log("Documentum file formats have been successfully loaded", null, null);
        return documentumFileFormat;
    }

    protected static Logger logger = Logger.getLogger("SSCController: ");

}
