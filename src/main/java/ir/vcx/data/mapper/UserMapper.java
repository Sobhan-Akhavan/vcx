package ir.vcx.data.mapper;

import ir.vcx.data.entity.VCXUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    ir.vcx.api.model.VCXUser entityToApi(VCXUser dataLayerObj);
}
