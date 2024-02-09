package ir.vcx.domain.model;

import ir.vcx.data.entity.VideoType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Sobhan Akhavan on 2/9/2024 - vcx
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VideoTypeReport {
    private VideoType videoType;
    private long count;
    private float percent;
}
