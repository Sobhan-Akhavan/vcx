package ir.vcx.domain.service;

import com.fanapium.keylead.client.users.ClientModifiableUser;
import com.fanapium.keylead.common.KeyleadUserVo;
import ir.vcx.data.entity.VCXUser;
import ir.vcx.data.repository.UserRepository;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by Sobhan on 11/16/2023 - VCX
 */

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserUtil userUtil;


    @Autowired
    public UserService(UserRepository userRepository, UserUtil userUtil) {
        this.userRepository = userRepository;
        this.userUtil = userUtil;
    }


    @Transactional
    public VCXUser getOrCreatePodUser(ClientModifiableUser userInfo) {

        Optional<VCXUser> userBySsoId = userRepository.getUserBySsoId(userInfo.getUserId());

        KeyleadUserVo keyleadUserVo = userInfo.getUserInfo();

        String userFullName = getUserFullName(keyleadUserVo);

        VCXUser vcxUser;

        if (!userBySsoId.isPresent()) {

            vcxUser = userRepository.addUser(userInfo.getUserId(), keyleadUserVo.getPreferred_username(), userFullName, keyleadUserVo.getPicture());

        } else {

            vcxUser = userBySsoId.get();

            boolean modify = false;

            if (!keyleadUserVo.getPreferred_username().equals(vcxUser.getUsername())) {
                vcxUser.setUsername(keyleadUserVo.getPreferred_username());
                modify = true;
            }
            if (userFullName != null && !userFullName.equals(vcxUser.getName())) {
                vcxUser.setName(userFullName);
                modify = true;
            }
            if (keyleadUserVo.getPicture() != null && !keyleadUserVo.getPicture().equals(vcxUser.getAvatar())) {
                vcxUser.setAvatar(keyleadUserVo.getPicture());
                modify = true;
            }

            if (modify) {
                vcxUser = userRepository.updateUser(vcxUser);
            }
        }

        return vcxUser;
    }

    private String getUserFullName(KeyleadUserVo userInfo) {

        String name = "";
        if (userInfo.getGiven_name() != null) {
            name += userInfo.getGiven_name();
        }
        if (userInfo.getFamily_name() != null) {
            name += name.isEmpty() ? userInfo.getFamily_name() : " " + userInfo.getFamily_name();
        }
        if (name.trim().isEmpty()) {
            name = userInfo.getPreferred_username();
        }
        return name;
    }

    @Transactional
    public VCXUser getUser(ClientModifiableUser clientModifiableUser) throws VCXException {
        return userRepository.getUserBySsoId(clientModifiableUser.getUserId())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.USER_NOT_FOUND));
    }
}
