package ir.vcx.domain.service;

import com.fanapium.keylead.client.users.ClientModifiableUser;
import com.fanapium.keylead.common.KeyleadUserVo;
import ir.vcx.api.model.IdentityType;
import ir.vcx.api.model.Order;
import ir.vcx.api.model.Paging;
import ir.vcx.data.entity.VCXUser;
import ir.vcx.data.repository.UserRepository;
import ir.vcx.exception.VCXException;
import ir.vcx.exception.VCXExceptionStatus;
import ir.vcx.util.LimitUtil;
import ir.vcx.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Sobhan on 11/16/2023 - VCX
 */

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserUtil userUtil;
    private final ThreadPoolExecutor threadPoolExecutor;


    @Autowired
    public UserService(UserRepository userRepository, UserUtil userUtil, @Qualifier("adminThreadPool") ThreadPoolExecutor threadPoolExecutor) {
        this.userRepository = userRepository;
        this.userUtil = userUtil;
        this.threadPoolExecutor = threadPoolExecutor;
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


    public Pair<List<VCXUser>, Long> searchOnUsers(String identity, IdentityType identityType, Paging paging) throws VCXException {

        VCXUser vcxAdminUser = Optional.ofNullable(userUtil.getCredential().getUser())
                .orElseThrow(() -> new VCXException(VCXExceptionStatus.UNAUTHORIZED));

        LimitUtil.validateInput(Arrays.asList(IdentityType.SSO_ID, IdentityType.USERNAME), identityType);

        LimitUtil.validateInput(Arrays.asList(Order.CREATED, Order.UPDATED, Order.USERNAME, Order.NAME, Order.SSO_ID), paging.getOrder());

        if (StringUtils.isNotBlank(identity) && identityType == null) {
            throw new VCXException(VCXExceptionStatus.INVALID_IDENTITY_TYPE);
        }

        if (StringUtils.isBlank(identity) && identityType != null) {
            throw new VCXException(VCXExceptionStatus.INVALID_IDENTITY);
        }

        Future<List<VCXUser>> searchOnUserThread = threadPoolExecutor.submit(() ->
                userRepository.searchOnUser(identity, identityType, paging));

        Future<Long> searchOnUserCountThread = threadPoolExecutor.submit(() ->
                userRepository.searchOnUserCount(identity, identityType));

        try {
            List<VCXUser> vcxUsers = searchOnUserThread.get();
            Long vcxUsersCount = searchOnUserCountThread.get();

            return Pair.of(vcxUsers, vcxUsersCount);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();

            throw new VCXException(VCXExceptionStatus.UNKNOWN_ERROR);
        }

    }
}
