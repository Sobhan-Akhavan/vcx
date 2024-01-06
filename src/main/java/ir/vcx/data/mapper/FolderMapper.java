package ir.vcx.data.mapper;

import ir.vcx.data.entity.VCXFolder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FolderMapper {

    FolderMapper INSTANCE = Mappers.getMapper(FolderMapper.class);

    ir.vcx.api.model.VCXFolder entityToApi(VCXFolder dataLayerObj);

}
