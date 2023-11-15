package br.com.souza.facialrecognition.service;

import br.com.souza.facialrecognition.dto.FaceAuthenticationResponse;
import br.com.souza.facialrecognition.handler.NotHumanFaceException;
import br.com.souza.facialrecognition.handler.NotRegisteredFaceException;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.Attribute;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.ResourceAlreadyExistsException;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FaceAuthenticationService {

    private final AmazonS3 s3client;
    private final AmazonRekognition rekognitionClient;
    private static final float SIMILARITY_PERCENTAGE = 95L;
    private static final String COLLECTION_ID = "users-photos";
    private static final String BUCKET_NAME = System.getenv("BUCKET_NAME");

    public FaceAuthenticationService(AmazonS3 s3client,
                                     AmazonRekognition rekognitionClient) {
        this.s3client = s3client;
        this.rekognitionClient = rekognitionClient;
    }

    public void saveNewPhoto(MultipartFile photo) throws Exception {
        if (!isFace(photo.getBytes())) {
            throw new NotHumanFaceException();
        }

        File file = new File(Objects.requireNonNull(photo.getOriginalFilename()));
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(photo.getBytes());
        }

        s3client.putObject(new PutObjectRequest(BUCKET_NAME, photo.getOriginalFilename(), file));
        file.delete();

        try {
            rekognitionClient.createCollection(new CreateCollectionRequest()
                    .withCollectionId(COLLECTION_ID));
        } catch (ResourceAlreadyExistsException e) {
            log.warn("Collection já existente");
        } catch (Exception e) {
            log.error("Erro ao criar collection");
            throw e;
        }

        rekognitionClient.indexFaces(new IndexFacesRequest()
                .withImage(new Image()
                        .withS3Object(new com.amazonaws.services.rekognition.model.S3Object()
                                .withBucket(BUCKET_NAME)
                                .withName(photo.getOriginalFilename())))
                .withCollectionId(COLLECTION_ID)
                .withExternalImageId(photo.getOriginalFilename())
                .withDetectionAttributes("ALL"));
    }

    public FaceAuthenticationResponse getAuthenticationByFace(MultipartFile photo) throws Exception {
        if (!isFace(photo.getBytes())) {
            throw new NotHumanFaceException();
        }

        File file = new File(Objects.requireNonNull(photo.getOriginalFilename()));
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(photo.getBytes());
        }
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, photo.getOriginalFilename() + "temp", file));
        file.delete();

        SearchFacesByImageResult result = rekognitionClient.searchFacesByImage(new SearchFacesByImageRequest()
                .withCollectionId(COLLECTION_ID)
                .withImage(new Image()
                        .withS3Object(new com.amazonaws.services.rekognition.model.S3Object()
                                .withBucket(BUCKET_NAME)
                                .withName(photo.getOriginalFilename() + "temp")))
                .withFaceMatchThreshold(SIMILARITY_PERCENTAGE));

        s3client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, photo.getOriginalFilename() + "temp"));

        if (result.getFaceMatches().isEmpty()) {
            throw new NotRegisteredFaceException();
        }

        FaceMatch matchedFace = result.getFaceMatches().stream()
                .max(Comparator.comparing(f -> f.getFace().getConfidence()))
                .orElseThrow(Exception::new);

        return FaceAuthenticationResponse.builder()
                .userName(matchedFace.getFace().getExternalImageId())
                .similarityPercentage(BigDecimal.valueOf(matchedFace.getFace().getConfidence()))
                .build();
    }

    private Boolean isFace(byte[] photo) {
        DetectFacesRequest request = new DetectFacesRequest()
                .withImage(new Image().withBytes(ByteBuffer.wrap(photo)))
                .withAttributes(Attribute.ALL);

        DetectFacesResult result = rekognitionClient.detectFaces(request);
        if (result.getFaceDetails().isEmpty()) {
            return false;
        }
        return result.getFaceDetails().get(0).getConfidence() > 90;
    }
}
