package com.akgarg.ecommerce.service.firebase;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
public interface FirebaseService {

    String upload(MultipartFile multipartFile, String fileName);

    boolean delete(String fileUrl);

}
