package com.example.bookreadingonline.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Table(name = "chapter")
@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Chapter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "order_chap")
    private Integer orderChap;

    @Column(name = "file_key")
    private String fileKey;

    @Column(name = "book_id")
    private Integer bookId;
}
