package tn.esprit.medicareaitigerback.models.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(name = "role_seq", sequenceName = "role_sequence", allocationSize = 1)
    private Long id;
    @Column(nullable = false, length = 50)
    private String name;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
    private List<Agent> agents = new ArrayList<>();
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
    @CreatedDate
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    @LastModifiedDate
    private Date modifiedAt;

    public Role(String name) {
        this.name = name;
    }

}

