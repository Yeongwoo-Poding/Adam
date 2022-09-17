package project.adam.entity.common;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.ZonedDateTime;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    private ZonedDateTime createdDate;
    private ZonedDateTime lastModifiedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = ZonedDateTime.now();
        this.lastModifiedDate = this.createdDate;
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = ZonedDateTime.now();
    }

    public boolean isModified() {
        return createdDate != lastModifiedDate;
    }
}
