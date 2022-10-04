package project.adam.entity.common;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = this.createdDate;
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }

    public boolean isModified() {
        return !createdDate.isEqual(lastModifiedDate);
    }
}
