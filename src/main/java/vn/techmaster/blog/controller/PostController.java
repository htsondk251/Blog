package vn.techmaster.blog.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import vn.techmaster.blog.controller.Attribute;
import vn.techmaster.blog.controller.request.LoginRequest;
import vn.techmaster.blog.model.Post;
import vn.techmaster.blog.model.User;
import vn.techmaster.blog.repository.PostRepository;
import vn.techmaster.blog.repository.UserRepository;
import vn.techmaster.blog.security.CookieManager;
import vn.techmaster.blog.service.iPostService;

@Controller
public class PostController {
  @Autowired private CookieManager cookieManager;
  @Autowired private iPostService postService;
  @Autowired private UserRepository userRepository;
  @Autowired private PostRepository postRepository;

  @GetMapping("/posts")
  public String getAllPosts(Model model, HttpServletRequest request) {
    List<Post> posts = new ArrayList<>();
    User user = null;
    
    String userEmail = cookieManager.getAuthenticatedEmail(request);
    //TODO: handle exception
    // if (userEmail != null) {
    //   return Route.ALLPOSTS;
    // } else {
    //   return Route.REDIRECT_HOME;
    // }
    
    Optional<User> userOptional = userRepository.findByEmail(userEmail);
    if (userOptional.isPresent()) {
      user = userOptional.get();
      posts = postService.getAllPostOfUser(user);
    }
    List<User> others = userRepository.findAll().stream()
                          .filter(u -> u != userOptional.get()).collect(Collectors.toList());
    
    model.addAttribute(Attribute.USER, user);
    model.addAttribute(Attribute.AUTHOR, user);
    model.addAttribute("others", others);
    model.addAttribute(Attribute.POSTS, posts);
    //TODO: cut the content to 1 short sentence
    return Route.ALLPOSTS;
  }

  @GetMapping("/post/new")
  public String addNewPost(Model model, HttpServletRequest request) {
    User user = null;
    String userEmail = cookieManager.getAuthenticatedEmail(request);
    Optional<User> userOptional = userRepository.findByEmail(userEmail);
    if (userOptional.isPresent()) {
      user = userOptional.get();
    }

    model.addAttribute(Attribute.USER, user);
    model.addAttribute(Attribute.POST, new Post());
    return "handlePost";
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
      postRepository.save(post);
      user.addPost(post);
    } else {
      Post postToUpdate = postService.findByUserAndId(user, id);
      postToUpdate.setTitle(post.getTitle());
      postToUpdate.setContent(post.getContent());
    }
    userRepository.save(user);
    return Route.REDIRECT_POSTS;
  }

  @GetMapping("/post/{postId}")
  public String getPost(@PathVariable("postId") Long id, Model model, HttpServletRequest request) {
    User user = null;
    Post post = null;
    User author = null;
    String userEmail = cookieManager.getAuthenticatedEmail(request);
    Optional<User> userOptional = userRepository.findByEmail(userEmail);
    if (userOptional.isPresent()) {
      user = userOptional.get();
      post = postRepository.findById(id).get();
      author = post.getAuthor();
    }

    model.addAttribute(Attribute.USER, user);
    model.addAttribute(Attribute.POST, post);
    model.addAttribute(Attribute.AUTHOR, author);
    //TODO: find why not have "comments" attribute, app still run: not show, not error notify, but run?
    model.addAttribute(Attribute.COMMENTS, post.getComments());
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
      post = postService.findByUserAndId(user, id);
    }

    model.addAttribute(Attribute.USER, user);
    model.addAttribute(Attribute.POST, post);
    return "handlePost";
  }

  @GetMapping("/post/delete/{id}")
  public String deletePost(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
    User user = null;
    Post post = null;
    String userEmail = cookieManager.getAuthenticatedEmail(request);
    Optional<User> userOptional = userRepository.findByEmail(userEmail);
    if (userOptional.isPresent()) {
      user = userOptional.get();
      post = postRepository.getOne(id);
      user.removePost(post);
      userRepository.flush();
    }
    model.addAttribute(Attribute.USER, user);
    model.addAttribute(Attribute.POSTS, user.getPosts());
    return Route.REDIRECT_POSTS;
  }

  @GetMapping("/{userId}/other/{authorId}")
  public String viewOtherPosts(@PathVariable("userId") Long userId, @PathVariable("authorId") Long authorId, Model model, HttpServletRequest request) {
    User user = null;
    User author = null;
    
    String userEmail = cookieManager.getAuthenticatedEmail(request);
    Optional<User> userOptional = userRepository.findByEmail(userEmail);
    if (userOptional.isPresent()) {
      user = userOptional.get();
      author = userRepository.findById(authorId).get();
    }

    model.addAttribute(Attribute.USER, user);
    model.addAttribute(Attribute.AUTHOR, author);
    model.addAttribute(Attribute.POSTS, author.getPosts());
    return Route.ALLPOSTS;
  }

}
