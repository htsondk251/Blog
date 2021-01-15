package vn.techmaster.blog.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import vn.techmaster.blog.Route;
import vn.techmaster.blog.controller.request.LoginRequest;
import vn.techmaster.blog.controller.request.PostInput;
import vn.techmaster.blog.model.Post;
import vn.techmaster.blog.model.User;
import vn.techmaster.blog.repository.PostRepository;
import vn.techmaster.blog.repository.UserRepository;
import vn.techmaster.blog.service.iPostService;



@Controller
public class PostController {

  final String LOGIN_REQUEST = "loginRequest";
  final String LOGIN_TEMPLATE = "login";
  final String LOGIN_COOKIE = "loginsuccess";
  final String REDIRECT_POSTS = "redirect:/posts";
  @Autowired
  private iPostService postService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PostRepository postRepository;

  @GetMapping("/posts")
  public String getAllPosts(Model model, HttpServletRequest request) {
    String userEmail = "";
    List<Post> posts = new ArrayList<>();
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (var cookie:cookies) {
        if (cookie.getName().equals(LOGIN_COOKIE)) {
          userEmail = cookie.getValue();
        }
      }
    }
    Optional<User> user = userRepository.findByEmail(userEmail);
    if (user.isPresent()) {
      posts = postService.getAllPostOfUser(user.get());
    }
    model.addAttribute("user", user.get());
    model.addAttribute("posts", posts);
    return "posts";
  }

  @GetMapping("/new")
  public String addNewPost(Model model, HttpServletRequest request) {
    String userEmail = "";
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (var cookie:cookies) {
        if (cookie.getName().equals(LOGIN_COOKIE)) {
          userEmail = cookie.getValue();
        }
      }
    }
    Optional<User> user = userRepository.findByEmail(userEmail);
    if (user.isPresent()) {
      model.addAttribute("user", user.get());
    }
    model.addAttribute("post", new Post());

    return "new";
  }

  @PostMapping("/new")
  public String addPostSuccess(@ModelAttribute Post post, BindingResult bindingResult, Model model, HttpServletRequest request) {
    // if (!bindingResult.hasErrors()) {        
    //   try {
    //     authenService.login(loginRequest);
    //     cookieManager.setAuthenticated(response, loginRequest.getEmail());          
    //     return REDIRECT_POSTS; 
    //   } catch (AuthenException e) {
    //     model.addAttribute(LOGIN_REQUEST, new LoginRequest(loginRequest.getEmail(), ""));
    //     model.addAttribute("errorMessage", e.getMessage());
    //     return LOGIN_TEMPLATE;
    //   }
    // }

    String userEmail = "";
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (var cookie:cookies) {
        if (cookie.getName().equals(LOGIN_COOKIE)) {
          userEmail = cookie.getValue();
        }
      }
    }
    // Post post = new Post();
    // post.setTitle(postInput.getTitle());
    // post.setContent(postInput.getContent());

    // postRepository.save(post);

    Optional<User> userOptional = userRepository.findByEmail(userEmail);
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      user.addPost(post);
      userRepository.save(user);
      
    }

    return REDIRECT_POSTS;
  }
}
