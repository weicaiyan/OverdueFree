package com.overduefree;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.overduefree.config.AuthProperties;
import com.overduefree.module.admin.entity.AdminUser;
import com.overduefree.module.admin.mapper.AdminUserMapper;
import com.overduefree.module.article.entity.Article;
import com.overduefree.module.article.mapper.ArticleMapper;
import com.overduefree.module.asset.entity.AssetResource;
import com.overduefree.module.asset.mapper.AssetResourceMapper;
import com.overduefree.module.caseitem.entity.SuccessCase;
import com.overduefree.module.caseitem.mapper.SuccessCaseMapper;
import com.overduefree.module.configitem.entity.SysConfig;
import com.overduefree.module.configitem.mapper.SysConfigMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final int NOT_DELETED = 0;
    private static final String ASSET_HOME_VIDEO = "HOME_VIDEO";
    private static final String ASSET_HOME_VIDEO_COVER = "HOME_VIDEO_COVER";
    private static final String ASSET_AI_CONSULT_BANNER = "AI_CONSULT_BANNER";
    private static final String ASSET_LOAN_CALCULATOR_BANNER = "LOAN_CALCULATOR_BANNER";
    private static final String ASSET_DEBT_PLAN_BANNER = "DEBT_PLAN_BANNER";
    private static final String ASSET_WECHAT_QR = "WECHAT_QR";

    private final SysConfigMapper sysConfigMapper;
    private final AdminUserMapper adminUserMapper;
    private final AssetResourceMapper assetResourceMapper;
    private final ArticleMapper articleMapper;
    private final SuccessCaseMapper successCaseMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthProperties authProperties;

    public DataInitializer(SysConfigMapper sysConfigMapper,
                           AdminUserMapper adminUserMapper,
                           AssetResourceMapper assetResourceMapper,
                           ArticleMapper articleMapper,
                           SuccessCaseMapper successCaseMapper,
                           PasswordEncoder passwordEncoder,
                           AuthProperties authProperties) {
        this.sysConfigMapper = sysConfigMapper;
        this.adminUserMapper = adminUserMapper;
        this.assetResourceMapper = assetResourceMapper;
        this.articleMapper = articleMapper;
        this.successCaseMapper = successCaseMapper;
        this.passwordEncoder = passwordEncoder;
        this.authProperties = authProperties;
    }

    @Override
    public void run(String... args) {
        initSystemConfig();
        initBossAccount();
        initAssets();
        initArticles();
        initSuccessCases();
    }

    private void initSystemConfig() {
        boolean exists = existsConfig(SysConfig.CUSTOMER_LOGIN_EXPIRE_DAYS);
        if (exists) {
            return;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(SysConfig.CUSTOMER_LOGIN_EXPIRE_DAYS);
        config.setConfigValue(String.valueOf(authProperties.getCustomerTokenDays()));
        config.setDescription("客户登录有效期，单位：天");
        sysConfigMapper.insert(config);
    }

    private boolean existsConfig(String configKey) {
        LambdaQueryWrapper<SysConfig> query = new LambdaQueryWrapper<SysConfig>()
            .eq(SysConfig::getConfigKey, configKey);
        return sysConfigMapper.selectCount(query) > 0;
    }

    private void initBossAccount() {
        if (adminUserMapper.selectCount(null) > 0) {
            return;
        }
        AdminUser boss = new AdminUser();
        boss.setUsername(authProperties.getInitBossUsername());
        boss.setPasswordHash(passwordEncoder.encode(authProperties.getInitBossPassword()));
        boss.setDisplayName("老板");
        boss.setRole(AdminUser.ROLE_BOSS);
        boss.setStatus(AdminUser.STATUS_ACTIVE);
        adminUserMapper.insert(boss);
    }

    private void initAssets() {
        Map<String, String> assets = new LinkedHashMap<>();
        assets.put(ASSET_HOME_VIDEO, "首页宣传视频");
        assets.put(ASSET_HOME_VIDEO_COVER, "首页视频封面");
        assets.put(ASSET_AI_CONSULT_BANNER, "AI债务咨询师入口图");
        assets.put(ASSET_LOAN_CALCULATOR_BANNER, "网贷利率计算器入口图");
        assets.put(ASSET_DEBT_PLAN_BANNER, "规划优化债务入口图");
        assets.put(ASSET_WECHAT_QR, "企业微信二维码");
        assets.forEach(this::insertAssetIfAbsent);
    }

    private void insertAssetIfAbsent(String assetKey, String title) {
        LambdaQueryWrapper<AssetResource> query = new LambdaQueryWrapper<AssetResource>()
            .eq(AssetResource::getAssetKey, assetKey);
        if (assetResourceMapper.selectCount(query) > 0) {
            return;
        }
        AssetResource resource = new AssetResource();
        resource.setAssetKey(assetKey);
        resource.setTitle(title);
        resource.setStatus(AssetResource.STATUS_ACTIVE);
        assetResourceMapper.insert(resource);
    }

    private void initArticles() {
        if (articleMapper.selectCount(null) > 0) {
            return;
        }
        insertArticle("网贷逾期多久有影响", "了解逾期后的常见影响和沟通准备。", 30);
        insertArticle("如何保护好自己的第二张身份证", "征信相关基础知识和注意事项。", 20);
        insertArticle("网贷逾期后如何协商还款", "整理沟通前需要准备的基本资料。", 10);
    }

    private void insertArticle(String title, String summary, int sortOrder) {
        Article article = new Article();
        article.setTitle(title);
        article.setSummary(summary);
        article.setContentText(summary + " 当前为演示内容，正式文案后续由运营方替换。");
        article.setPublishTime(LocalDateTime.now());
        article.setSortOrder(sortOrder);
        article.setStatus(Article.STATUS_PUBLISHED);
        article.setDeleted(NOT_DELETED);
        articleMapper.insert(article);
    }

    private void initSuccessCases() {
        if (successCaseMapper.selectCount(null) > 0) {
            return;
        }
        insertSuccessCase("花女士", "138****6357", "360借条", "5折结清（满足条件）", "114200");
        insertSuccessCase("尤先生", "150****8014", "安逸花|有利网", "拉长还款期限3-5年", "190200");
        insertSuccessCase("孙女士", "155****0588", "悟空理财|360金融", "6折结清", "73600");
    }

    private void insertSuccessCase(String name, String phone, String platforms, String plan, String amount) {
        SuccessCase successCase = new SuccessCase();
        successCase.setDisplayName(name);
        successCase.setMaskedPhone(phone);
        successCase.setOverduePlatforms(platforms);
        successCase.setOverdueAmount(new BigDecimal(amount));
        successCase.setHandlingPlan(plan);
        successCase.setDetailText("当前为演示案例，正式案例需脱敏后由运营方替换。");
        successCase.setSortOrder(10);
        successCase.setStatus(SuccessCase.STATUS_PUBLISHED);
        successCase.setDeleted(NOT_DELETED);
        successCaseMapper.insert(successCase);
    }
}
