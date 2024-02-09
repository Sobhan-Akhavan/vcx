package ir.vcx.domain.model;

import ir.vcx.data.entity.GenreType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Sobhan Akhavan on 2/9/2024 - vcx
 */

@Setter
@Getter
@NoArgsConstructor
public class GenreTypeReport {
    private GenreType genreType;
    private long count;
    private float percent;

    public GenreTypeReport(GenreType genreType, long count, float percent) {
        this.genreType = genreType;
        this.count = count;
        this.percent = percent;
    }

    public GenreTypeReport(GenreType genreType, long count) {
        this.genreType = genreType;
        this.count = count;
    }
}
