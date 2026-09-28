package tn.esprit.medicareaitigerback.models.entities;

import tn.esprit.medicareaitigerback.models.enumeration.AttachmentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Audited
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_seq")
    @SequenceGenerator(name = "attachment_seq", sequenceName = "attachment_sequence", allocationSize = 1)
    private Long id;

    private String fileName;
    private String fileDirectory;
    private String fileType;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private AttachmentType type;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Patient patient;
    @JsonIgnore
    @ManyToOne
    private Doctor doctor;

    @JsonIgnore
    @ManyToOne
    private Agent agent;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @CreatedBy
    private User createdBy;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @LastModifiedBy
    private User updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date modifiedAt;
}