package vn.techmaster.blog.controller;

import java.util.List;
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
import vn.techmaster.blog.repository.PostRepository;
import vn.techmaster.blog.repository.UserRepository;
import vn.techmaster.blog.security.CookieManager;
import vn.techmaster.blog.service.iPostService;

@Controller
public class CommentController {
    @Autowired CookieManager cookieManager;
    // @Autowired iPostService postService;
    // @Autowired ICommentService commentServiceImpl;
    @Autowired CommentRepository commentRepository;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;

    @GetMapping("/comment/new/{id}")
    public String addComment(@PathVariable Long id, HttpServletRequest request, Model model) {
        // User user = null;
        // String userEmail = cookieManager.getAuthenticatedEmail(request);
        // Optional<User> userOptional = userRepository.findByEmail(userEmail);
        // if (userOptional.isPresent()) {
        // user = userOptional.get();
        // }
        // Post post = postService.findByUserAndId(user, postId);
        model.addAttribute("postId", id);
        // model.addAttribute("user", user);
        model.addAttribute("comment", new Comment());

        return "newComment";
    }

    @PostMapping("/comment/save")
    public String handleAddComment(@ModelAttribute Comment comment, @RequestParam(value = "postId") Long postId,
            BindingResult bindingResult, HttpServletRequest request, Model model) {
        Post post = postRepository.getOne(postId);
        String userEmail = cookieManager.getAuthenticatedEmail(request);
        User commenter = userRepository.findByEmail(userEmail).get();
        
        commentRepository.save(comment);
        commenter.addComment(comment);
        userRepository.save(commenter);
        post.addComment(comment);
        userRepository.save(commenter);
        userRepository.save(post.getAuthor());
        // List<Comment> comments = post.getComments();

        model.addAttribute("user", commenter);
        model.addAttribute("post", post);
        model.addAttribute("comments", post.getComments());
        return "post";
    }

    @GetMapping("/comment/remove/{id}")
    public String removeComment(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        // Comment commentToBeDeleted = null;
        Post post = null;
        User author = null;
        String userEmail = cookieManager.getAuthenticatedEmail(request);
        User commenter = userRepository.findByEmail(userEmail).get();
        Comment commentToBeDeleted = commentRepository.getOne(id);
        // if (commentOptional.isPresent()) {
            // commentToBeDeleted = commentOptional.get();
            post = commentToBeDeleted.getPost();
            author = post.getAuthor();
            post.removeComment(commentToBeDeleted);
            postRepository.save(post);
            userRepository.save(commenter);
            userRepository.save(author);
        // } else {
        //     throw new IllegalArgumentException("comment not exist");
        // }

        model.addAttribute("user", commenter);
        model.addAttribute("post", post);
        model.addAttribute("comments", post.getComments());
        return "post";
    }

}
