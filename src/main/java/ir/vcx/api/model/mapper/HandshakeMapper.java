package ir.vcx.api.model.mapper;

import ir.vcx.api.model.Handshake;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Mapper
public interface HandshakeMapper {

    HandshakeMapper INSTANCE = Mappers.getMapper(HandshakeMapper.class);

    Handshake mapToApi(ir.vcx.domain.model.sso.otp.Handshake handshake);

}
