package vn.techmaster.blog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import vn.techmaster.blog.model.Post;
import vn.techmaster.blog.model.User;
import vn.techmaster.blog.repository.PostRepository;
import vn.techmaster.blog.repository.UserRepository;
import vn.techmaster.blog.service.IAuthenService;
import vn.techmaster.blog.service.iPostService;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@DataJpaTest
@Sql("/user.sql")
public class PostRepositoryTest {
  @Autowired
  UserRepository userRepository;

  // @Autowired
  // iPostService postService;

  @Autowired
  PostRepository postRepository;
  
  User userBob, userAlice;
  Post post1, post2, post3;

  @BeforeEach
  public void setUp() {
    System.out.println("setup");
    userBob = userRepository.findByEmail("bob@gmail.com").get();
    userAlice = userRepository.findByEmail("alice@gmail.com").get();
    post1 = new Post();
    post2 = new Post();
    post3 = new Post();

    post1.setTitle("first post");
    post1.setContent("something1");

    post2.setTitle("second post");
    post2.setContent("something2");

    post3.setTitle("third post");
    post3.setContent("something3");
    // postRepository.save(post);
    
    userBob.addPost(post1);
    userBob.addPost(post2);
    userAlice.addPost(post3);
    System.out.println("finish setup");
  }
  @Test
  public void insertTest() {
    assertThat(postRepository.findAll().size()).isEqualTo(1);
    assertThat(postRepository.findById(1L).get().getAuthor().getFullname()).isEqualTo("Bob");
  }

  @Test
  public void queryTest() {
    // List<Post> posts = postService.getAllPostOfUser(user);
    List<Post> bobPosts = userBob.getPosts();
    List<Post> alicePosts = userAlice.getPosts();
    assertThat(bobPosts.size()).isEqualTo(2);
    assertThat(bobPosts.get(0).getTitle()).isEqualTo("first post");
    assertThat(alicePosts.size()).isEqualTo(1);
    assertThat(alicePosts.get(0).getTitle()).isEqualTo("first post");

  }
}
