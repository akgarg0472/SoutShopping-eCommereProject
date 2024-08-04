package com.akgarg.ecommerce.service.firebase;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Service
public class FirebaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseService.class);

    private final String firebaseJsonConfigPath;
    private final String firebaseStorageBucketId;
    private final String firebaseStorageMediaBaseUrl;

    private Credentials credentials;

    public FirebaseService(
            @Value("${firebase.json.config.path}") final String firebaseJsonConfigPath,
            @Value("${firebase.storage.blob.bucked.id}") final String firebaseStorageBucketId,
            @Value("${firebase.storage.media.base-url}") final String firebaseStorageMediaBaseUrl
    ) {
        this.firebaseJsonConfigPath = firebaseJsonConfigPath;
        this.firebaseStorageBucketId = firebaseStorageBucketId;
        this.firebaseStorageMediaBaseUrl = firebaseStorageMediaBaseUrl;
    }

    @PostConstruct
    void init() throws IOException {
        this.credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get(firebaseJsonConfigPath)));
    }

    public String upload(final MultipartFile multipartFile, final String fileName) {
        try {
            LOGGER.info("uploading file to firebase with name: {}", fileName);
            final File file = convertMultipartToFile(multipartFile, fileName);
            final String uploadResult = uploadFile(file, fileName);
            LOGGER.info("file upload result: {}", uploadResult);
            Files.delete(Paths.get(file.getAbsolutePath()));
            return uploadResult;
        } catch (Exception e) {
            LOGGER.error("Error uploading file to firebase", e);
            return "error";
        }
    }

    public boolean delete(final String fileUrl) {
        boolean result = false;

        try {
            final String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1, fileUrl.lastIndexOf('?'));
            LOGGER.info("deleting file from firebase storage: {}", fileName);
            final Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            final BlobId blobId = BlobId.of(firebaseStorageBucketId, fileName);
            result = storage.delete(blobId);
        } catch (Exception e) {
            LOGGER.error("Error deleting file from firebase", e);
        }

        return result;
    }

    private String uploadFile(File file, String fileName) throws IOException {
        final BlobId blobId = BlobId.of(firebaseStorageBucketId, fileName);
        final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        final Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        return String.format(firebaseStorageMediaBaseUrl, URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8)));
    }

    private File convertMultipartToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);

        try (final FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }

        return tempFile;
    }

}
