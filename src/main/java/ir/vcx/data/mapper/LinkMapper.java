package ir.vcx.data.mapper;

import ir.vcx.data.entity.VCXDownloadLink;
import ir.vcx.data.entity.VCXLink;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LinkMapper {

    LinkMapper INSTANCE = Mappers.getMapper(LinkMapper.class);

    ir.vcx.api.model.VCXLink uploadLinkEntityToApi(VCXLink dataLayerObj);

    ir.vcx.api.model.VCXLink downloadLinkEntityToApi(VCXDownloadLink dataLayerObj);
}
