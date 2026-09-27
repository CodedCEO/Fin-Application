package com.finapp.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;

@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public class BaseEntity {

//    @CreatedDate
    @Column( updatable = false)
    private LocalDateTime createdAt;


//    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

//    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

//    @LastModifiedBy
    @Column(insertable = false)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.createdBy == null) {
            this.createdBy = "SYSTEM"; // Fallback default value
        }
    }

    // 3. Pro-Tip: Add a companion hook to handle modifications automatically
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.updatedBy == null) {
            this.updatedBy = "SYSTEM"; // Fallback default value
        }
    }
}
