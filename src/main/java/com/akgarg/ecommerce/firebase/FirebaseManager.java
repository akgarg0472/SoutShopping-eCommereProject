package com.akgarg.ecommerce.firebase;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Component
public class FirebaseManager {

    private String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("ecommerce-93f42.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();

        String jsonPath = System.getProperty("user.dir") + "//src//main//java//com//akgarg//ecommerce//firebase//" +
                "ecommerce-93f42-firebase-adminsdk-pwf1j-9094406a7d.json";
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        return String.format("https://firebasestorage.googleapis.com/v0/b/ecommerce-93f42.appspot.com/o/%s?alt=media"
                , URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8)));
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        return tempFile;
    }

    public String upload(MultipartFile multipartFile, String fileName) {
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

    public boolean delete(String url) {
        boolean result = false;

        String fileName = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('?'));
        String jsonPath = System.getProperty("user.dir") + "//src//main//java//com//akgarg//ecommerce//firebase//" +
                "ecommerce-93f42-firebase-adminsdk-pwf1j-9094406a7d.json";

        try {
            Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath));
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            BlobId blobId = BlobId.of("ecommerce-93f42.appspot.com", fileName);
            result = storage.delete(blobId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
