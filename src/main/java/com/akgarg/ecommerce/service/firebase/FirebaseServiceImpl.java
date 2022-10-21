package com.akgarg.ecommerce.service.firebase;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
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
public class FirebaseServiceImpl implements FirebaseService {

    private Credentials credentials;

    @PostConstruct
    void init() throws IOException {
        final String configJsonPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "firebase-config.json";
        this.credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get(configJsonPath)));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public String upload(final MultipartFile multipartFile, final String fileName) {
        try {
            File file = this.convertToFile(multipartFile, fileName);
            String uploadResult = this.uploadFile(file, fileName);
            file.delete();
            return uploadResult;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    public boolean delete(final String fileUrl) {
        final String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1, fileUrl.lastIndexOf('?'));
        boolean result = false;

        try {
            final Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            final BlobId blobId = BlobId.of("ecommerce-93f42.appspot.com", fileName);
            result = storage.delete(blobId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String uploadFile(File file, String fileName) throws IOException {
        final BlobId blobId = BlobId.of("ecommerce-93f42.appspot.com", fileName);
        final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        final Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        return String.format("https://firebasestorage.googleapis.com/v0/b/ecommerce-93f42.appspot.com/o/%s?alt=media", URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8)));
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);

        try (final FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }

        return tempFile;
    }

}
