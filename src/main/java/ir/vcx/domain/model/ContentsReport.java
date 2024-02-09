package ir.vcx.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by Sobhan Akhavan on 2/9/2024 - vcx
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContentsReport {
    private List<VideoTypeReport> videoTypeReports;
    private List<GenreTypeReport> genreTypeReports;
    private long totalCount;
}
