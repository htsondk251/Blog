package vn.techmaster.blog;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import vn.techmaster.blog.model.Comment;
import vn.techmaster.blog.model.Post;
import vn.techmaster.blog.model.User;
import vn.techmaster.blog.repository.PostRepository;
import vn.techmaster.blog.repository.UserRepository;
import vn.techmaster.blog.repository.CommentRepository;
import vn.techmaster.blog.service.IAuthenService;
import vn.techmaster.blog.service.iPostService;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@DataJpaTest
@Sql("/user.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentRepositoryTest {
  
  @Autowired CommentRepository commentRepository;

  @Autowired UserRepository userRepository;
  
  @Autowired PostRepository postRepository;

  User userBob, userAlice, userDuy;
  Post post1, post2, post3;
  Comment comment1, comment2, comment3, comment4;

  @BeforeEach
  public void setUp() {
    System.out.println("setup");

    userBob = userRepository.findByEmail("bob@gmail.com").get();
    userAlice = userRepository.findByEmail("alice@gmail.com").get();
    userDuy = userRepository.findByEmail("duy@gmail.com").get();
    post1 = new Post("1st post", "something1");
    post2 = new Post("2nd post", "something2");
    post3 = new Post("3rd post", "something2");

    // postRepository.save(post1);
    // postRepository.save(post2);
    // postRepository.save(post3);

    userBob.addPost(post1);
    userBob.addPost(post2);
    userAlice.addPost(post3);

    userRepository.flush();

    comment1 = new Comment("alice's comment - post 1");
    comment2 = new Comment("bob's comment - post 1");
    comment3 = new Comment("alice's comment - post 2");
    comment4 = new Comment("duy's comment - post 3");

    // commentRepository.save(comment1);
    // commentRepository.save(comment2);
    // commentRepository.save(comment3);
    // commentRepository.save(comment4);

    userAlice.addComment(comment1);
    userBob.addComment(comment2);
    userAlice.addComment(comment3);
    userDuy.addComment(comment4);

    post1.addComment(comment1);
    post1.addComment(comment2);
    post2.addComment(comment3);
    post3.addComment(comment4);

    // userRepository.save(userAlice);   //add comment4 first, then comment 1,2,3
    // userRepository.save(userBob);
    // userRepository.save(userAlice);
    // userRepository.save(userDuy);

    userRepository.flush();

    System.out.println("finish setup");
  }

  @AfterEach
  public void clear() {
    commentRepository.deleteAll();
    postRepository.deleteAll();;
    userRepository.deleteAll();
  }

  @Test
  public void insertionTest() {
    List<Comment> comments = commentRepository.findAll();
    Comment comment1Check = commentRepository.findById(1L).get();

    assertThat(commentRepository.findAll().size()).isEqualTo(4);
    // assertThat(commentRepository.findById(1L).get().getCommenter().getFullname()).isEqualTo("Alice");
    // assertThat(commentRepository.findById(1L).get().getPost().getComments().size()).isEqualTo(2);
    // assertThat(commentRepository.findById(1L).get().getPost().getComments().get(1).getCommenter().getFullname()).isEqualTo("Bob");
  }

  @Test
  public void queryTest() {
    List<Comment> aliceComments = userAlice.getComments();
    List<Comment> bobComments = userBob.getComments();
    assertThat(bobComments.size()).isEqualTo(1);
    assertThat(bobComments.get(0).getContent()).isEqualTo("bob's comment - post 1");
    assertThat(aliceComments.size()).isEqualTo(2);
    assertThat(aliceComments.get(1).getContent()).isEqualTo("alice's comment - post 2");

  }

  @Test
  public void deleteFromPostRepositoryTest(){
    post1.removeComment(comment2);
    userBob.removeComment(comment2);

    // userRepository.save(userBob);
    // userRepository.save(userAlice);

    userRepository.flush();

    List<Comment> aliceComments = userAlice.getComments();
    List<Comment> bobComments = userBob.getComments();

    aliceComments.stream().forEach(System.out::println);
    bobComments.stream().forEach(System.out::println);

    assertThat(post1.getComments().size()).isEqualTo(1);
    assertThat(userBob.getComments().size()).isEqualTo(0);
  }
}
