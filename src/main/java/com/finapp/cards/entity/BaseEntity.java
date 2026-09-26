package com.finapp.cards.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
    @MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
    @Getter
    @Setter
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


    }
