package ir.vcx.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ApiPageList<T> implements VCXApiModel {

    private Object entity;
    private List<T> list;
    private Long count;

    public ApiPageList(Object entity) {
        this.entity = entity;
    }

    public ApiPageList(List<T> list, Long count) {
        this.list = list;
        this.count = count;
    }

}
