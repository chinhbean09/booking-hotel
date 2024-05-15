package com.chinhbean.bookinghotel.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;

import java.util.Date;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass

public class BaseEntity {

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "modified_date")
    private Date modifiedDate;

    @PrePersist
    protected void onCreate(){
        createdDate = new Date();
    }

    @PreUpdate
    protected void onUpdate(){
        modifiedDate = new Date();
    }

}
