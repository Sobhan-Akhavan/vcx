package ir.vcx.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class ApiPageList<T> implements VCXApiModel {

    private Object entity;
    private Set<T> set;
    private Long count;

    public ApiPageList(Object entity) {
        this.entity = entity;
    }

    public ApiPageList(Set<T> set, Long count) {
        this.set = set;
        this.count = count;
    }

}
