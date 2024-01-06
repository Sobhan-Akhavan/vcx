package ir.vcx.data.mapper;

import ir.vcx.data.entity.VCXLink;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LinkMapper {

    LinkMapper INSTANCE = Mappers.getMapper(LinkMapper.class);

    ir.vcx.api.model.VCXLink entityToApi(VCXLink dataLayerObj);
}
