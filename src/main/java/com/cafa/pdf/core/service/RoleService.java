package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.RoleDTO;
import com.cafa.pdf.core.dal.dao.IBaseDAO;
import com.cafa.pdf.core.dal.dao.RoleDAO;
import com.cafa.pdf.core.dal.entity.Role;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.role.RoleSaveReq;
import com.cafa.pdf.core.web.request.role.RoleUpdateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleService extends ABaseService<Role, Long> {

    private final RoleDAO roleDAO;

    @Autowired
    public RoleService(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    @Override
    protected IBaseDAO<Role, Long> getEntityDAO() {
        return roleDAO;
    }

    public Page<RoleDTO> findAll(BasicSearchReq basicSearchReq) {

        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        Page<Role> rolePage = this.findAll(pageNumber, basicSearchReq.getPageSize());

        return rolePage.map(this::getRoleDTO);
    }



    @Transactional
    public void update(RoleUpdateReq roleUpdateReq) {
        Role role = this.findById(roleUpdateReq.getId());
        ObjectConvertUtil.objectCopy(role, roleUpdateReq);
        this.update(role);
    }

    public boolean exist(String name) {
        return roleDAO.findByName(name) != null;
    }

    @Transactional
    public void save(RoleSaveReq roleSaveReq) {
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

    /**
     * 转为DTO
     * @param source Role
     * @return RoleDTO
     */
    private RoleDTO getRoleDTO(Role source) {
        RoleDTO roleDTO = new RoleDTO();
        ObjectConvertUtil.objectCopy(roleDTO, source);
        return roleDTO;
    }

}
