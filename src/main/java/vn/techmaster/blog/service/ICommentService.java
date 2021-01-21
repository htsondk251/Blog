package vn.techmaster.blog.service;

import java.util.List;

import vn.techmaster.blog.model.Comment;

public interface ICommentService {
    List<Comment> findByPostID(Long postId);
}
