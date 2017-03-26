package com.cafa.pdf.core.service;

import com.cafa.pdf.core.dal.dto.PermissionDTO;
import com.cafa.pdf.core.dal.entity.Permission;
import com.cafa.pdf.core.dal.request.BasicSearchReq;
import com.cafa.pdf.core.dal.request.permission.PermissionSaveReq;
import com.cafa.pdf.core.dal.request.permission.PermissionUpdateReq;
import com.cafa.pdf.core.repository.IBaseDAO;
import com.cafa.pdf.core.repository.PermissionDAO;
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
public class PermissionService extends ABaseService<Permission, Long> {

    @Autowired
    private PermissionDAO permissionDAO;

    @Override
    protected IBaseDAO<Permission, Long> getEntityDAO() {
        return permissionDAO;
    }

    public Page<PermissionDTO> findAll(BasicSearchReq basicSearchReq) {

        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        Page<Permission> permissionPage = this.findAll(pageNumber, basicSearchReq.getPageSize());

        return permissionPage.map(source -> {
            PermissionDTO roleDTO = new PermissionDTO();
            ObjectConvertUtil.objectCopy(roleDTO, source);
            return roleDTO;
        });
    }

    @Transactional
    public void updateByReq(PermissionUpdateReq permissionUpdateReq) {

        Permission permission = this.findById(permissionUpdateReq.getId());
        ObjectConvertUtil.objectCopy(permission, permissionUpdateReq);
        this.update(permission);
    }

    public boolean exist(String permit) {
        return permissionDAO.findByPermit(permit) != null;
    }

    @Transactional
    public void saveByReq(PermissionSaveReq permissionSaveReq) {

        Permission permission = new Permission();
        ObjectConvertUtil.objectCopy(permission, permissionSaveReq);
        this.save(permission);
    }

    public List<Permission> listByRoleId(Long roleId){
        return permissionDAO.findByRoleId(roleId);
    }

    /*不建议如此强硬，该手动去除关联再删除
    @Transactional(readOnly = false)
    public void delete(Long permissionId){
        //先删除t_role_permission表的外键关联
        customizedDAO.deleteRolePermissionRelation(permissionId);
        //再删除permission
        permissionDAO.delete(permissionId);
    }*/


}
