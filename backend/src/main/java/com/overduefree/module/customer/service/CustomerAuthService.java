package com.overduefree.module.customer.service;

import com.overduefree.module.customer.dto.CustomerLoginRequest;
import com.overduefree.module.customer.dto.CustomerLoginResult;
import com.overduefree.module.customer.dto.CustomerMeResult;
import com.overduefree.module.customer.dto.SendCodeRequest;
import com.overduefree.module.customer.dto.SendCodeResult;

public interface CustomerAuthService {

    SendCodeResult sendCode(SendCodeRequest request);

    CustomerLoginResult login(CustomerLoginRequest request);

    void logout();

    CustomerMeResult me();
}
