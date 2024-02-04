package ir.vcx.api.model;

import ir.vcx.data.entity.GenreType;
import ir.vcx.data.entity.VideoType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VCXContent {

    private String name;
    private String hash;
    private String verticalPoster;
    private String horizontalPoster;
    private VCXFolder parentFolder;
    private String description;
    private VideoType videoType;
    private Set<GenreType> genresType;
}
