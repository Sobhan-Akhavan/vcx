package ir.vcx.data.mapper;

import ir.vcx.data.entity.VCXPlan;
import ir.vcx.data.entity.VCXUser;
import ir.vcx.data.entity.VCXUserLimit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Created by Sobhan Akhavan on 1/20/2024 - vcx
 */

@Mapper
public interface UserLimitMapper {

    UserLimitMapper INSTANCE = Mappers.getMapper(UserLimitMapper.class);

    @Mapping(source = "plan", target = "plan", qualifiedByName = "planMapper")
    @Mapping(source = "user", target = "user", qualifiedByName = "userMapper")
    ir.vcx.api.model.VCXUserLimit entityToApi(VCXUserLimit dataLayerObj);

    @Named("planMapper")
    default ir.vcx.api.model.VCXPlan planMapper(VCXPlan plan) {
        return plan == null ? null : PlanMapper.INSTANCE.entityToApi(plan);
    }

    @Named("userMapper")
    default ir.vcx.api.model.VCXUser userMapper(VCXUser user) {
        return user == null ? null : UserMapper.INSTANCE.entityToApi(user);
    }
}
