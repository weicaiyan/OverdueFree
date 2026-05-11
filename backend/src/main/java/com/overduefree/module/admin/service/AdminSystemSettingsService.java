package com.overduefree.module.admin.service;

import com.overduefree.module.admin.dto.AdminSystemSettingsResult;
import com.overduefree.module.admin.dto.AdminSystemSettingsUpdateRequest;

public interface AdminSystemSettingsService {

    AdminSystemSettingsResult getSettings();

    AdminSystemSettingsResult updateSettings(AdminSystemSettingsUpdateRequest request);
}
