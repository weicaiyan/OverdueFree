package com.overduefree.module.admin.service;

import com.overduefree.module.admin.dto.AdminLoginRequest;
import com.overduefree.module.admin.dto.AdminLoginResult;
import com.overduefree.module.admin.dto.AdminMeResult;

public interface AdminAuthService {

    AdminLoginResult login(AdminLoginRequest request);

    void logout();

    AdminMeResult me();
}
