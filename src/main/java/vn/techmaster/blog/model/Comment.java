package vn.techmaster.blog.model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Comment")
@Table(name = "COMMENT")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String content;
  @DateTimeFormat(pattern = "dd/MM/yyyy - HH:mm")
  private LocalDateTime lastUpdate;
  @PrePersist //Trước khi lưu khi khởi tạo record
  public void prePersist() {
      lastUpdate = LocalDateTime.now();
  }
  @PreUpdate //Khi cập nhật record
  public void preUpdate() {
      lastUpdate = LocalDateTime.now();
  }

  public Comment(String content) {
    this.content = content;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  private User commenter; //Mỗi comment phải do một commenter viết

  @ManyToOne(fetch = FetchType.LAZY)
  private Post post; //Mỗi comment phải gắn vào một post

  @Override
  public String toString() {
    return "Comment [commenter=" + commenter + ", id=" + id + ", post=" + post + "]";
  }
 

}