package ir.vcx.data.mapper;

import ir.vcx.data.entity.GenreType;
import ir.vcx.data.entity.VCXContent;
import ir.vcx.data.entity.VCXFolder;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface ContentMapper {
    ContentMapper INSTANCE = Mappers.getMapper(ContentMapper.class);

    @Mapping(source = "genresType", target = "genresType", qualifiedByName = "genresTypeMapper")
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
    @Named("genresTypeMapper")
    default Set<GenreType> genresTypeMapper(Set<GenreType> genresTypes) {
        if (genresTypes == null || !Hibernate.isInitialized(genresTypes)) {
            return null;
        } else {
            return genresTypes;
        }
    }

}
