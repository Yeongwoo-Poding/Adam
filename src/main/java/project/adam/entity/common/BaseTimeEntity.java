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

    private ZonedDateTime createDate;
    private ZonedDateTime lastModifiedDate;

    @PrePersist
    public void prePersist() {
        this.createDate = ZonedDateTime.now();
        this.lastModifiedDate = ZonedDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = ZonedDateTime.now();
    }
}
