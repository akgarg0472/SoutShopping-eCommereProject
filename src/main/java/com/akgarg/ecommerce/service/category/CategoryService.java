package com.akgarg.ecommerce.service.category;

import com.akgarg.ecommerce.model.dto.Message;
import com.akgarg.ecommerce.model.entity.Category;
import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.repository.CategoryRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.service.firebase.FirebaseService;
import com.akgarg.ecommerce.service.user.UserService;
import com.akgarg.ecommerce.utils.EcommerceUtils;
import com.akgarg.ecommerce.utils.ImageNameConstants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final FirebaseService firebaseService;
    private final ProductRepository productRepository;
    private final UserService userService;

    public String addCategory(final Category category, final MultipartFile image, final HttpSession session) {
        try {
            LOGGER.info("received request to add category: {}", category);
            final Optional<Category> categoryOptional = this.categoryRepository.findById(category.getName());

            if (categoryOptional.isPresent()) {
                session.setAttribute("addCategoryResponse", new Message("Category " + category.getName() + " already exists", "alert-danger"));
                return "redirect:/admin/dashboard";
            }
        } catch (Exception e) {
            LOGGER.error("error checking if category exists", e);
        }

        String imageFileName = "null";

        try {
            if (image != null && !image.isEmpty()) {
                String originalImageFileName = image.getOriginalFilename().substring(0, image.getOriginalFilename().lastIndexOf('.'));
                String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
                imageFileName = EcommerceUtils.getEncodedImageFileName(category.getName(), originalImageFileName, extension);
            }

            if (!imageFileName.equals("null")) {
                String uploadResult = this.firebaseService.upload(image, imageFileName);

                if (!uploadResult.equals("error")) {
                    category.setPicture(uploadResult);
                } else {
                    category.setPicture(ImageNameConstants.DEFAULT_CATEGORY_IMAGE);
                }
            } else {
                category.setPicture(ImageNameConstants.DEFAULT_CATEGORY_IMAGE);
            }

            this.categoryRepository.save(category);
            LOGGER.info("category added successfully: {}", category);
            session.setAttribute("addCategoryResponse", new Message("Category " + category.getName() + " added successfully", "alert-success"));
        } catch (Exception e) {
            LOGGER.error("error adding product category", e);
            session.setAttribute("addCategoryResponse", new Message("Error adding " + category.getName() + " category", "alert-success"));
        }

        return "redirect:/admin/dashboard";
    }

    public String manageCategories(final HttpSession session, final Principal principal, final Model model) {
        List<Category> categories = this.categoryRepository.findAll();
        User admin = this.userService.getUserByEmail(principal.getName());

        session.setAttribute("categories", categories);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", EcommerceUtils.getDayHour());

        return "admin/all-categories";
    }

    public String updateCategory(final Category category, final MultipartFile image, final HttpSession session) {
        try {
            LOGGER.info("received request to update category: {}", category);
            String imageFileName = "null";

            if (image != null && !image.isEmpty()) {
                String originalImageFileName = image.getOriginalFilename().substring(0, image.getOriginalFilename().lastIndexOf('.'));
                String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
                imageFileName = EcommerceUtils.getEncodedImageFileName(category.getName(), originalImageFileName, extension);
            }

            if (!imageFileName.equals("null")) {
                String uploadResult = this.firebaseService.upload(image, imageFileName);

                if (!uploadResult.equals("error")) {
                    category.setPicture(uploadResult);
                    this.categoryRepository.save(category);
                    session.setAttribute("editCategoryResponse", new Message("Category updated successfully", ""));
                } else {
                    session.setAttribute("addCategoryResponse", new Message("Error updating " + category.getName() + " category", "alert-success"));
                }
            }
        } catch (Exception e) {
            LOGGER.error("error updating category", e);
            session.setAttribute("addCategoryResponse", new Message("Error updating " + category.getName() + " category", "alert-success"));
        }

        return "redirect:/admin/manage-categories";
    }

    public String deleteCategory(final String categoryToRemove, final HttpSession session) {
        try {
            LOGGER.info("received request to delete category: {}", categoryToRemove);
            final Optional<Category> categoryOptional = this.categoryRepository.findById(categoryToRemove);

            if (!categoryOptional.isPresent()) {
                LOGGER.warn("category not found with name={} to delete", categoryToRemove);
                throw new IllegalStateException("No category found with name: " + categoryToRemove);
            }

            String imageUrl = categoryOptional.get().getPicture();

            if (!imageUrl.equals(ImageNameConstants.DEFAULT_CATEGORY_IMAGE)) {
                boolean deleteResult = this.firebaseService.delete(imageUrl);

                if (deleteResult) {
                    this.categoryRepository.deleteById(categoryToRemove);
                    this.productRepository.deleteProductByCategory(categoryToRemove);
                    session.setAttribute("deleteCategoryResponse", new Message("Category and all related products deleted", "alert-success"));
                } else {
                    session.setAttribute("deleteCategoryResponse", new Message("Category deletion failed", "alert-success"));
                }
            } else {
                this.categoryRepository.deleteById(categoryToRemove);
                this.productRepository.deleteProductByCategory(categoryToRemove);
                session.setAttribute("deleteCategoryResponse", new Message("Category and all related products deleted", "alert-success"));
            }
        } catch (Exception e) {
            LOGGER.error("error deleting category", e);
            session.setAttribute("deleteCategoryResponse", new Message("Category deletion failed", "alert-success"));
        }

        return "redirect:/admin/manage-categories";
    }

}
