package vn.techmaster.blog.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.techmaster.blog.model.Comment;
import vn.techmaster.blog.model.Post;
import vn.techmaster.blog.model.User;
import vn.techmaster.blog.repository.CommentRepository;
import vn.techmaster.blog.repository.UserRepository;
import vn.techmaster.blog.security.CookieManager;
import vn.techmaster.blog.service.CommentServiceImpl;
import vn.techmaster.blog.service.ICommentService;
import vn.techmaster.blog.service.iPostService;


@Controller
public class CommentController {
    @Autowired CommentRepository commentRepository;
    @Autowired ICommentService commentServiceImpl;
    @Autowired CookieManager cookieManager;
    @Autowired UserRepository userRepository;
    @Autowired iPostService postService;

    @GetMapping("/comment/new/{id}")
    public String addComment(@PathVariable("id") Long postId, HttpServletRequest request, Model model) {
        User user = null;
        String userEmail = cookieManager.getAuthenticatedEmail(request);
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isPresent()) {
          user = userOptional.get();
        }
        // Post post = postService.findByUserAndId(user, postId);
        model.addAttribute("postId", postId);
        model.addAttribute("user", user);
        model.addAttribute("comment", new Comment());

        return "newComment";
    }

    @PostMapping("/comment/save")
    public String handleAddComment(@ModelAttribute Comment comment, @RequestParam(value="postId") Long postId, BindingResult bindingResult, HttpServletRequest request, Model model) {
        User user = null;
        String userEmail = cookieManager.getAuthenticatedEmail(request);
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isPresent()) {
          user = userOptional.get();
          user.addComment(comment);
          userRepository.save(user);
        }
        Post post = postService.findByUserAndId(user, postId);
        post.addComment(comment);
        userRepository.save(post.getAuthor());
        
        model.addAttribute("user", user);

        return "post";
    }

    @GetMapping("/comment/remove/{id}")
    public String removeComment(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        Comment commentToBeDeleted = null;
        Long postId = null;
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isPresent()) {
            commentToBeDeleted = commentOptional.get();
            postId = commentToBeDeleted.getPost().getId();
            commentRepository.delete(commentToBeDeleted);
        } else {
            throw new IllegalArgumentException("comment not exist");
        }
        
        model.addAttribute("user", request.getAttribute("user"));
        model.addAttribute("posts", request.getAttribute("posts"));
        model.addAttribute("comments", commentServiceImpl.findByPostID(postId));
        return "post";
    }
    

}
