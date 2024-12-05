package com.example.bookreadingonline.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Table(name = "comment")
@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false)
    private Integer id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "title")
    private String title;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "reply_count")
    private Integer replyCount;

    @Column(name = "parent")
    private Integer parent;

    @Column(name = "reply_name")
    private String replyName;

    @Column(name = "chapter_id")
    private Integer chapterId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "book_id")
    private Integer bookId;
}
