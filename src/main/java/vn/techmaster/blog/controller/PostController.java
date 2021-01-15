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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import vn.techmaster.blog.controller.request.LoginRequest;
import vn.techmaster.blog.model.Post;
import vn.techmaster.blog.model.User;
import vn.techmaster.blog.repository.PostRepository;
import vn.techmaster.blog.repository.UserRepository;
import vn.techmaster.blog.security.CookieManager;
import vn.techmaster.blog.service.iPostService;

@Controller
public class PostController {

  // private final String LOGIN_COOKIE = "loginsuccess";
  @Autowired private iPostService postService;

  @Autowired private UserRepository userRepository;

  // @Autowired private PostRepository postRepository;

  @Autowired private CookieManager cookieManager;

  @GetMapping("/posts")
  public String getAllPosts(Model model, HttpServletRequest request) {
    List<Post> posts = new ArrayList<>();
    String userEmail = cookieManager.getAuthenticatedEmail(request);
    //TODO: handle exception
    // if (userEmail != null) {
    //   return Route.ALLPOSTS;
    // } else {
    //   return Route.REDIRECT_HOME;
    // }
    
    Optional<User> user = userRepository.findByEmail(userEmail);
    if (user.isPresent()) {
      posts = postService.getAllPostOfUser(user.get());
    }
    model.addAttribute("user", user.get());
    model.addAttribute("posts", posts);
    return Route.ALLPOSTS;
  }

  @GetMapping("/new")
  public String addNewPost(Model model, HttpServletRequest request) {
    User user = null;
    String userEmail = cookieManager.getAuthenticatedEmail(request);
    Optional<User> userOptional = userRepository.findByEmail(userEmail);
    if (userOptional.isPresent()) {
      user = userOptional.get();
    }

    model.addAttribute("user", user);
    model.addAttribute("post", new Post());

    return "new";
  }

  @PostMapping("/post/save")
  public String addPostSuccess(@ModelAttribute Post post, BindingResult bindingResult, Model model, HttpServletRequest request) {
    //TODO: handle exception
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
    User user = null;
    String userEmail = cookieManager.getAuthenticatedEmail(request);
    Optional<User> userOptional = userRepository.findByEmail(userEmail);
    if (userOptional.isPresent()) {
      user = userOptional.get();
    }
    Long id = post.getId();
    if (id == null) {
      user.addPost(post);
      userRepository.save(user);
    } else {
      Post postToUpdate = postService.findById(user, id);
      postToUpdate.setTitle(post.getTitle());
      postToUpdate.setContent(post.getContent());
      userRepository.save(user);
    }
    return Route.REDIRECT_POSTS;
  }

  @GetMapping("/post/{id}")
  public String getPost(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
    User user = null;
    Post post = null;
    String userEmail = cookieManager.getAuthenticatedEmail(request);
    Optional<User> userOptional = userRepository.findByEmail(userEmail);
    if (userOptional.isPresent()) {
      user = userOptional.get();
      post = postService.findById(user, id);
    }

    model.addAttribute("user", user);
    model.addAttribute("post", post);
    return "post";
  }

  @GetMapping("/post/edit/{id}")
  public String editPost(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
    User user = null;
    Post post = null;
    String userEmail = cookieManager.getAuthenticatedEmail(request);
    Optional<User> userOptional = userRepository.findByEmail(userEmail);
    if (userOptional.isPresent()) {
      user = userOptional.get();
      post = postService.findById(user, id);
    }

    model.addAttribute("user", user);
    model.addAttribute("post", post);
    return "new";
  }


}
