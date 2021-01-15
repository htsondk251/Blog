package vn.techmaster.blog.service;

import java.util.List;
import java.util.Optional;

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

  @Override
  public Post findById(User user, Long id) {
    List<Post> posts = getAllPostOfUser(user);
    Optional<Post> post = posts.stream().filter(p -> p.getId() == id).findFirst();
    if (post != null) {
      return post.get();
    } else {
      return null;
    }
  }
  
}
