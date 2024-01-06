package ir.vcx.data.mapper;

import ir.vcx.data.entity.VCXContent;
import ir.vcx.data.entity.VCXFolder;
import ir.vcx.data.entity.VCXPoster;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface ContentMapper {
    ContentMapper INSTANCE = Mappers.getMapper(ContentMapper.class);

    @Mapping(source = "posters", target = "posters", qualifiedByName = "postersMapper")
    @Mapping(source = "parentFolder", target = "parentFolder", qualifiedByName = "folderMapper")
    ir.vcx.api.model.VCXContent entityToApi(VCXContent dataLayerObj);


    @Named("folderMapper")
    default ir.vcx.api.model.VCXFolder folderMapper(VCXFolder folder) {
        return folder == null ? null : FolderMapper.INSTANCE.entityToApi(folder);
    }

    @Named("postersMapper")
    default Set<ir.vcx.api.model.VCXPoster> postersMapper(Set<VCXPoster> posters) {
        return posters.stream()
                .map(PosterMapper.INSTANCE::entityToApi)
                .collect(Collectors.toSet());
    }

}
