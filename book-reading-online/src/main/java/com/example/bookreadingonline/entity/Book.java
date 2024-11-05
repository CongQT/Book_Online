package com.example.bookreadingonline.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;
import java.util.List;

@Table(name = "book")
@SQLDelete(sql = "UPDATE book SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at is null")
@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Book extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false)
    private Integer id;

    @Column(name = "author_id")
    private Integer authorId;

    @Column(name = "title")
    private String title;

    @Column(name = "summary")
    private String summary;

    @Column(name = "avg_rating")
    private Double avgRating;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "view")
    private Integer view;

    @Column(name = "status")
    private String status;

    @OneToMany()
    @NotAudited
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private List<CategoryBook> categoryBooks;

}
