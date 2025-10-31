package org.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name="claims-service")
public interface ClaimClient {

    ///Leveraging through Feign-Client to consume API endpoint from claims-service to enable conversion of any MIME types into String base64 format
    ///With Feign-Client enables the seamless communication between the backend microservices
    @PostMapping(value= "v1/claim/convertDocs-base64",consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //serializing and specify the form data value consuming
    String convertDocsToStringBase64(MultipartFile document);

    @PostMapping(value= "v1/claim/convertDocs-base64/json-format",consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //serializing and specify the form data value consuming
    String convertDocsToStringBase64Json(MultipartFile document);

}
