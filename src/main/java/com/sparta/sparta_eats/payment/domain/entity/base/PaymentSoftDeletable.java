package com.sparta.sparta_eats.payment.domain.entity.base;


import com.sparta.sparta_eats.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@NoArgsConstructor
public class PaymentSoftDeletable extends BaseEntity {

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    protected String deletedBy;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * 서비스 계층에서 소프트 삭제 처리 시 호출
     */
    public void markDeleted(String deleter) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deleter;
    }

    /**
     *  실수 복구용(필요 시)
     */
    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
    }



}
