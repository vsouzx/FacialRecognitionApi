package br.com.souza.facialrecognition.controller.impl;

import br.com.souza.facialrecognition.controller.IFaceAuthenticationController;
import br.com.souza.facialrecognition.dto.FaceAuthenticationResponse;
import br.com.souza.facialrecognition.service.FaceAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/v1/auth")
public class FaceAuthenticationControllerImpl implements IFaceAuthenticationController {

    private final FaceAuthenticationService faceAuthenticationService;

    public FaceAuthenticationControllerImpl(FaceAuthenticationService faceAuthenticationService) {
        this.faceAuthenticationService = faceAuthenticationService;
    }

    @PostMapping(value = "/byface", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FaceAuthenticationResponse> getAuthenticationByFace(@RequestPart(value = "photo") MultipartFile photo) throws Exception {
        return new ResponseEntity<>(faceAuthenticationService.getAuthenticationByFace(photo), HttpStatus.OK);
    }

    @PostMapping(value = "/savenewphoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> saveNewPhoto(@RequestPart(value = "photo") MultipartFile photo) throws Exception {
        faceAuthenticationService.saveNewPhoto(photo);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
