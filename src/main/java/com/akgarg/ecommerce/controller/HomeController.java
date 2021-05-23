package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.entity.Category;
import com.akgarg.ecommerce.entity.Orders;
import com.akgarg.ecommerce.entity.Product;
import com.akgarg.ecommerce.entity.User;
import com.akgarg.ecommerce.repository.CategoryRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Controller
public class HomeController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    @Autowired
    public HomeController(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }


    @ModelAttribute
    public void checkLoginStatus(Principal principal,
                                 Model model) {
        if (principal == null) {
            model.addAttribute("isUserLoggedIn", null);
        } else {
            User user = this.userRepository.getUserByEmail(principal.getName());
            model.addAttribute("isUserLoggedIn", user);
            model.addAttribute("loggedInUserRole", user.getRole());
        }
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        List<Category> categories = this.categoryRepository.findAll();
        List<Product> products = this.productRepository.findAll();

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        return "home";
    }


    @RequestMapping(value = {"/login", "/login.html"}, method = RequestMethod.GET)
    public String login(Principal principal) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());

            if (user.getRole().equalsIgnoreCase("ROLE_USER")) {
                return "redirect:/user/dashboard";
            } else if (user.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            }
        }

        return "login";
    }


    @RequestMapping(value = {"/register", "/register.html"}, method = RequestMethod.GET)
    public String register(Principal principal) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());

            if (user.getRole().equalsIgnoreCase("ROLE_USER")) {
                return "redirect:/user/dashboard";
            } else if (user.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            }
        }

        return "register";
    }


    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String cart(Principal principal,
                       Model model) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());

            int totalCartPrice = 0;
            for (Orders o : user.getCart()) {
                totalCartPrice += o.getOrderPrice() * o.getProductQuantities();
            }

            model.addAttribute("userCartItems", user.getCart());
            model.addAttribute("totalCartPrice", totalCartPrice);
        }

        return "cart";
    }


    @RequestMapping(value = "/search/{searchKeywords}", method = RequestMethod.GET)
    public String searchProduct(@PathVariable("searchKeywords") String searchKeywords,
                                Model model) {
        List<Product> products = new ArrayList<>();
        String[] keywords = searchKeywords.split(" ");
        boolean isCategoryFound = false;

        for (String string : keywords) {
            if (string.length() > 2) {
                List<Product> category = this.productRepository.findProductsByCategoryContainsIgnoreCase(string);
                for (Product product : category) {
                    if (!products.contains(product)) {
                        products.add(product);
                        if (!isCategoryFound) {
                            System.out.println("called");
                            isCategoryFound = true;
                            model.addAttribute("probableSearchCategory", product.getCategory());
                        }
                    }
                }
            }
        }

        for (String string : keywords) {
            if (string.length() > 2) {
                List<Product> name = this.productRepository.findProductsByNameContainsIgnoreCase(string);
                for (Product product : name) {
                    if (!products.contains(product)) {
                        products.add(product);
                        if (!isCategoryFound) {
                            System.out.println("called");
                            isCategoryFound = true;
                            model.addAttribute("probableSearchCategory", product.getCategory());
                        }
                    }
                }
            }
        }

        for (String string : keywords) {
            if (string.length() > 2) {
                List<Product> description = this.productRepository.findProductsByDescriptionContainsIgnoreCase(string);
                for (Product product : description) {
                    if (!products.contains(product)) {
                        products.add(product);
                    }
                }
            }
        }

        if (model.getAttribute("probableSearchCategory") == null) {
            model.addAttribute("probableSearchCategory", "");
        }

        model.addAttribute("searchProducts", products);
        model.addAttribute("categories", this.categoryRepository.findAll());
        model.addAttribute("title", "SoutShopping - " + searchKeywords);

        return "search";
    }


    @RequestMapping(value = "/logout-success", method = RequestMethod.GET)
    public String logoutSuccess(HttpSession session) {
        session.setAttribute("logout", "");
        return "redirect:/login";
    }
}