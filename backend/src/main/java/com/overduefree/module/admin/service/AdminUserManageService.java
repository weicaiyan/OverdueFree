package com.overduefree.module.admin.service;

import com.overduefree.module.admin.dto.AdminPasswordResetRequest;
import com.overduefree.module.admin.dto.AdminUserCreateRequest;
import com.overduefree.module.admin.dto.AdminUserResult;
import com.overduefree.module.admin.dto.AdminUserUpdateRequest;

import java.util.List;

public interface AdminUserManageService {

    List<AdminUserResult> listUsers();

    AdminUserResult createUser(AdminUserCreateRequest request);

    AdminUserResult updateUser(Long id, AdminUserUpdateRequest request);

    void resetPassword(Long id, AdminPasswordResetRequest request);
}
