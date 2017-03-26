package com.cafa.pdf.core.service;

import com.cafa.pdf.core.dal.dto.RoleDTO;
import com.cafa.pdf.core.dal.entity.Role;
import com.cafa.pdf.core.dal.request.BasicSearchReq;
import com.cafa.pdf.core.dal.request.role.RoleSaveReq;
import com.cafa.pdf.core.dal.request.role.RoleUpdateReq;
import com.cafa.pdf.core.repository.IBaseDAO;
import com.cafa.pdf.core.repository.RoleDAO;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RoleService extends ABaseService<Role, Long> {

    @Autowired
    private RoleDAO roleDAO;

    @Override
    protected IBaseDAO<Role, Long> getEntityDAO() {
        return roleDAO;
    }

    public Page<RoleDTO> findAll(BasicSearchReq basicSearchReq) {

        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        Page<Role> rolePage = this.findAll(pageNumber, basicSearchReq.getPageSize());

        return rolePage.map(source -> {
            RoleDTO roleDTO = new RoleDTO();
            ObjectConvertUtil.objectCopy(roleDTO, source);
            return roleDTO;
        });
    }

    @Transactional
    public void updateByReq(RoleUpdateReq roleUpdateReq) {
        Role role = this.findById(roleUpdateReq.getId());
        ObjectConvertUtil.objectCopy(role, roleUpdateReq);
        this.update(role);
    }

    public boolean exist(String name) {
        return roleDAO.findByName(name) != null;
    }

    @Transactional
    public void saveByReq(RoleSaveReq roleSaveReq) {
        Role role = new Role();
        ObjectConvertUtil.objectCopy(role, roleSaveReq);
        this.save(role);
    }

    public List<Role> listByUserId(Long userId){
        return roleDAO.findByUserId(userId);
    }

    /*不建议如此强硬，该手动去除关联再删除
    @Transactional(readOnly = false)
    public void delete(Long roleId){
        //先删除t_user_role表的外键关联
        customizedDAO.deleteUserRoleRelation(roleId);
        //再删除role
        roleDAO.delete(roleId);
    }*/

}
