package com.cafa.pdf.core.service;

import com.cafa.pdf.core.dal.dto.UserDTO;
import com.cafa.pdf.core.dal.entity.User;
import com.cafa.pdf.core.dal.request.BasicSearchReq;
import com.cafa.pdf.core.dal.request.user.UserSaveReq;
import com.cafa.pdf.core.dal.request.user.UserSearchReq;
import com.cafa.pdf.core.dal.request.user.UserUpdateReq;
import com.cafa.pdf.core.extra.shiro.CryptographyUtil;
import com.cafa.pdf.core.repository.IBaseDAO;
import com.cafa.pdf.core.repository.UserDAO;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Scope("prototype")//Shiro的配置影响到了动态代理，现在就这样吧，可以去看MShiroRealm
@Service
@Transactional(readOnly = true)
public class UserService extends ABaseService<User, Long> {

    @Autowired
    private UserDAO userDAO;

    private static final String UNKNOW = "未知";
    private static final String AC = "激活/在职";
    private static final String UN = "冻结/离职";

    @Override
    protected IBaseDAO<User, Long> getEntityDAO() {
        return userDAO;
    }

    public User findByUsername(String username) {
        log.debug("username_{}没有缓存", username);
        return userDAO.findByUsername(username);
    }

    public boolean exist(String username) {
        return userDAO.findByUsername(username) != null;
    }

    public Long getCount() {
        log.debug("countAllUser没有缓存");
        return userDAO.count();
    }

    @Transactional(readOnly = false)
    public void delete(Long id) {
        // 并不是真正的删除，只是冻结账户
        User user = findById(id);
        user.setActive(0);
        update(user);
    }

    @Transactional
    public void updateByReq(UserUpdateReq userUpdateReq) {
        User user = findById(userUpdateReq.getId());
        ObjectConvertUtil.objectCopy(user, userUpdateReq);
        user.setModifiedTime(new Date());
        update(user);
    }

    @Transactional
    public void saveByReq(UserSaveReq userSaveReq) {

        if (exist(userSaveReq.getUsername())) {
            return;
        }

        User user = new User();
        ObjectConvertUtil.objectCopy(user, userSaveReq);
        user.setCreatedTime(new Date());
        user.setModifiedTime(new Date());
        user.setPassword(CryptographyUtil.cherishSha1(user.getPassword()));
        save(user);
    }

    public Page<UserDTO> findAll(UserSearchReq userSearchReq, BasicSearchReq basicSearchReq) {

        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        PageRequest pageRequest = super.buildPageRequest(pageNumber, basicSearchReq.getPageSize());

        //除了分页条件没有特定搜索条件的，为了缓存count
        if (ObjectConvertUtil.objectFieldIsBlank(userSearchReq)){
            log.debug("没有特定搜索条件的");
            List<User> userList = userDAO.listAllPaged(pageRequest);
            List<UserDTO> userDTOList = userList.stream().map(source -> {
                UserDTO userDTO = new UserDTO();
                ObjectConvertUtil.objectCopy(userDTO, source);
                userDTO.setActiveStr(source.getActive() == null ? UNKNOW : source.getActive() == 1 ? AC : UN);
                return userDTO;
            }).collect(Collectors.toList());

            //为了计算总数使用缓存，加快搜索速度
            Long count = getCount();
            return new PageImpl<>(userDTOList, pageRequest, count);
        }

        //有了其它搜索条件
        Page<User> userPage = super.findAllBySearchParams(
                buildSearchParams(userSearchReq), pageNumber, basicSearchReq.getPageSize());

        return userPage.map(source -> {
            UserDTO userDTO = new UserDTO();
            ObjectConvertUtil.objectCopy(userDTO, source);
            userDTO.setActiveStr(source.getActive() == null ? UNKNOW : source.getActive() == 1 ? AC : UN);
            return userDTO;
        });

    }

}
