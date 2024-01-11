package ir.vcx.data.mapper;

import ir.vcx.data.entity.VCXPlan;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlanMapper {

    PlanMapper INSTANCE = Mappers.getMapper(PlanMapper.class);

    ir.vcx.api.model.VCXPlan entityToApi(VCXPlan dataLayerObj);
}
