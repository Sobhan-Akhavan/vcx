package ir.vcx.data.mapper;

import ir.vcx.data.entity.GenreType;
import ir.vcx.data.entity.VCXContent;
import ir.vcx.data.entity.VCXFolder;
import ir.vcx.data.entity.VCXPoster;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface ContentMapper {
    ContentMapper INSTANCE = Mappers.getMapper(ContentMapper.class);

    @Mapping(source = "genresType", target = "genresType", qualifiedByName = "genresTypeMapper")
    @Mapping(source = "posters", target = "posters", qualifiedByName = "postersMapper")
    @Mapping(source = "parentFolder", target = "parentFolder", qualifiedByName = "folderMapper")
    ir.vcx.api.model.VCXContent entityToApi(VCXContent dataLayerObj);

    @Named("folderMapper")
    default ir.vcx.api.model.VCXFolder folderMapper(VCXFolder folder) {
        if (folder == null || !Hibernate.isInitialized(folder)) {
            return null;
        } else {
            return FolderMapper.INSTANCE.entityToApi(folder);
        }
    }

    @Named("postersMapper")
    default Set<ir.vcx.api.model.VCXPoster> postersMapper(Set<VCXPoster> posters) {
        return posters.stream()
                .map(PosterMapper.INSTANCE::entityToApi)
                .collect(Collectors.toSet());
    }

    @Named("genresTypeMapper")
    default Set<GenreType> genresTypeMapper(Set<GenreType> genresTypes) {
        return genresTypes;
    }

}
