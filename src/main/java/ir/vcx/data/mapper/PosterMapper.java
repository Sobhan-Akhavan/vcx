package ir.vcx.data.mapper;

import ir.vcx.data.entity.VCXPoster;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PosterMapper {
    PosterMapper INSTANCE = Mappers.getMapper(PosterMapper.class);

    ir.vcx.api.model.VCXPoster entityToApi(VCXPoster dataLayerObj);
}
