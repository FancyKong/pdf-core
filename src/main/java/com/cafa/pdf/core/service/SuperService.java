package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.RoleDTO;
import com.cafa.pdf.core.commom.dto.su.SuperPermissionDTO;
import com.cafa.pdf.core.commom.dto.su.SuperRoleDTO;
import com.cafa.pdf.core.commom.dto.su.SuperRolePermissionDTO;
import com.cafa.pdf.core.commom.dto.su.SuperUserRoleDTO;
import com.cafa.pdf.core.dal.dao.PermissionDAO;
import com.cafa.pdf.core.dal.dao.RoleDAO;
import com.cafa.pdf.core.dal.dao.UserDAO;
import com.cafa.pdf.core.dal.entity.Permission;
import com.cafa.pdf.core.dal.entity.Role;
import com.cafa.pdf.core.dal.entity.User;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import com.cafa.pdf.core.web.request.su.SuperRolePermissionReq;
import com.cafa.pdf.core.web.request.su.SuperUserRoleReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SuperService {

    private final RoleDAO roleDAO;
    private final UserDAO userDAO;
    private final PermissionDAO permissionDAO;

    @Autowired
    public SuperService(RoleDAO roleDAO, UserDAO userDAO, PermissionDAO permissionDAO) {
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
        this.permissionDAO = permissionDAO;
    }

    public List<RoleDTO> listAllRole() {
        return roleDAO.findAll().stream().map(role -> {
            return ObjectConvertUtil.objectCopy(new RoleDTO(), role);
        }).collect(Collectors.toList());
    }

    public SuperUserRoleDTO findByUsername(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) return null;

        SuperUserRoleDTO superUserRoleDTO = new SuperUserRoleDTO();
        List<Role> userRoles = roleDAO.findByUserId(user.getId());
        Set<Long> collect = userRoles.stream().map(Role::getId).collect(Collectors.toSet());
        List<Role> all = roleDAO.findAll();

        List<SuperRoleDTO> superRoleDTOS = all.stream().map(role -> {
            SuperRoleDTO superRoleDTO = new SuperRoleDTO();
            ObjectConvertUtil.objectCopy(superRoleDTO, role);
            if (collect.contains(role.getId())) {
                superRoleDTO.setHave(Boolean.TRUE);
            }else {
                superRoleDTO.setHave(Boolean.FALSE);
            }
            return superRoleDTO;
        }).collect(Collectors.toList());

        superUserRoleDTO.setUsername(username);
        superUserRoleDTO.setRoles(superRoleDTOS);
        return superUserRoleDTO;
    }

    @Transactional
    public void updateUserRole(SuperUserRoleReq superUserRoleReq) {

        String username = superUserRoleReq.getUsername();
        List<Long> roleIds = superUserRoleReq.getRoleIds();

        User user = userDAO.findByUsername(username);
        if (user != null) {
            List<Role> roles = roleIds.stream().map(roleId -> {
                Role role = new Role();
                role.setId(roleId);
                return role;
            }).collect(Collectors.toList());

            user.setRoles(roles);
            userDAO.save(user);
        }
    }

    public SuperRolePermissionDTO findByRolename(String rolename) {
        Role byName = roleDAO.findByName(rolename);
        if (byName == null) return null;

        List<Permission> rolePermissions = permissionDAO.findByRoleId(byName.getId());
        if (rolePermissions == null) return null;

        Set<Long> collect = rolePermissions.stream().map(Permission::getId).collect(Collectors.toSet());

        List<Permission> all = permissionDAO.findAll();
        SuperRolePermissionDTO superRolePermissionDTO = new SuperRolePermissionDTO();
        List<SuperPermissionDTO> permissionDTOs = all.stream().map(permission -> {
            SuperPermissionDTO dto = new SuperPermissionDTO();
            ObjectConvertUtil.objectCopy(dto, permission);
            if (collect.contains(permission.getId())) {
                dto.setHave(Boolean.TRUE);
            }else {
                dto.setHave(Boolean.FALSE);
            }
            return dto;
        }).collect(Collectors.toList());

        superRolePermissionDTO.setRolename(rolename);
        superRolePermissionDTO.setPermissions(permissionDTOs);

        return superRolePermissionDTO;
    }

    @Transactional
    public void updateRolePermission(SuperRolePermissionReq superRolePermissionReq) {
        String rolename = superRolePermissionReq.getRolename();
        List<Long> permissionIds = superRolePermissionReq.getPermissionIds();

        Role role = roleDAO.findByName(rolename);
        if (role != null) {
            List<Permission> permissions = permissionIds.stream().map(pId -> {
                Permission permission = new Permission();
                permission.setId(pId);
                return permission;
            }).collect(Collectors.toList());

            role.setPermissions(permissions);
            roleDAO.save(role);
        }
    }


}
