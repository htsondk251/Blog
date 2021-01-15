package vn.techmaster.blog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.techmaster.blog.model.Post;
import vn.techmaster.blog.model.User;
import vn.techmaster.blog.repository.UserRepository;

@Service
public class PostService implements iPostService {

  @Override
  public List<Post> getAllPostOfUser(User user) {
    return user.getPosts();
  }
  
}
