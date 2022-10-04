package project.adam.controller.dto;

import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class Paging {

    private final int pageNumber;
    private final int pageSize;
    private final int pageOffset;

    public Paging(Pageable pageable) {
        this.pageNumber = pageable.getPageNumber();
        this.pageSize = pageable.getPageSize();
        this.pageOffset = (int) pageable.getOffset();
    }
}
