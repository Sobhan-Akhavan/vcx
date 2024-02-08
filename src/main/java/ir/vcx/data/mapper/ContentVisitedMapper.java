package ir.vcx.data.mapper;

import ir.vcx.data.entity.VCXContent;
import ir.vcx.data.entity.VCXContentVisit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Created by Sobhan Akhavan on 2/8/2024 - vcx
 */

@Mapper
public interface ContentVisitedMapper {

    ContentVisitedMapper INSTANCE = Mappers.getMapper(ContentVisitedMapper.class);

    @Mapping(source = "content", target = "content", qualifiedByName = "contentMapper")
    @Mapping(source = "count", target = "visitCount")
    ir.vcx.api.model.VCXContentVisited entityToApi(VCXContentVisit dataLayerObj);

    @Named("contentMapper")
    default ir.vcx.api.model.VCXContent contentMapper(VCXContent content) {
        return content == null ? null : ContentMapper.INSTANCE.entityToApi(content);
    }
}
