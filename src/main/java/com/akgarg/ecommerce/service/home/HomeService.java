package com.akgarg.ecommerce.service.home;

import org.springframework.ui.Model;

import java.security.Principal;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
public interface HomeService {

    void home(Model model);

    void cart(Principal principal, Model model);

    void searchProduct(String searchKeywords, Model model);

}
