package vn.techmaster.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import vn.techmaster.blog.model.Comment;
import vn.techmaster.blog.repository.CommentRepository;

public class CommentServiceImpl implements ICommentService {

    @Autowired private CommentRepository commentRepository;

    @Override
    public List<Comment> findByPostID(Long postId) {
        return commentRepository.findAll().stream().filter(c -> c.getPost().getId()==postId).collect(Collectors.toList());
    }
    
}
