package tn.esprit.medicareaitigerback.models.entities;

import tn.esprit.medicareaitigerback.models.dto.request.AddressAddDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", sequenceName = "address_sequence", allocationSize = 1)
    private Long id;
    @Lob
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private Double longitude;
    @Column(nullable = false)
    private Double latitude;
    private String libelle;
    @Column(nullable = false)
    private Boolean isDefault = false;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    @CreatedDate
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    @LastModifiedDate
    private Date modifiedAt;

//    public void fromDTO(AddressAddDTO dto) {
//        this.address = dto.getAddress();
//        this.longitude = dto.getLongitude();
//        this.latitude = dto.getLatitude();
//        this.libelle = dto.getLibelle();
//    }
}
