package com.ly.cookbook.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.ly.cookbook.common.constant.RedisConstant;
import com.ly.cookbook.common.model.Result;
import com.ly.cookbook.model.dto.GenerateCodeDTO;
import com.ly.cookbook.model.dto.ValidateCodeDTO;
import com.ly.cookbook.model.vo.RegisterCodeVO;
import com.ly.cookbook.service.RegisterCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 注册码控制器
 * 说明: 注册码是由内部App用户生成的注册凭证（类似邀请码）
 * 权限: 只有已登录的App用户才能生成注册码
 *
 * @author admin
 * @date 2025-10-14
 */
@Slf4j
@RestController
@RequestMapping("/register-code")
@RequiredArgsConstructor
@Tag(name = "注册码管理", description = "注册码生成和验证相关接口（邀请码系统）")
public class RegisterCodeController {

    private final RegisterCodeService registerCodeService;

    /**
     * 生成注册码（需要登录）
     * 由内部App用户生成，用于邀请其他人注册
     * 失败时 Service 层会抛出异常，由全局异常处理器处理
     */
    @PostMapping("/generate")
    @Operation(summary = "生成注册码", description = "由内部App用户生成注册凭证，有效期为1天（需要登录）")
    @SaCheckRole({"VIP_USER","ADMIN"})
    public Result<RegisterCodeVO> generateCode(@Valid @RequestBody GenerateCodeDTO generateCodeDTO) {
        // 获取当前登录用户ID
        long userId = StpUtil.getLoginIdAsLong();
        log.info("生成注册码请求，用户ID：{}，备注：{}", userId, generateCodeDTO.getRemark());

        // 判断是单个生成还是批量生成
        Integer count = generateCodeDTO.getCount();
        if (count == null || count == 1) {
            // 单个生成
            String code = registerCodeService.generateRegisterCode(userId, generateCodeDTO.getRemark());

            RegisterCodeVO vo = RegisterCodeVO.builder()
                    .code(code)
                    .generatorUserId(userId)
                    .remark(generateCodeDTO.getRemark())
                    .expireTime(RedisConstant.REGISTER_CODE_EXPIRE_TIME)
                    .generateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();

            return Result.success(vo);
        } else {
            // 批量生成
            List<String> codes = registerCodeService.batchGenerateRegisterCodes(
                    userId, count, generateCodeDTO.getRemark()
            );

            RegisterCodeVO vo = RegisterCodeVO.builder()
                    .codes(codes)
                    .generatorUserId(userId)
                    .remark(generateCodeDTO.getRemark())
                    .expireTime(RedisConstant.REGISTER_CODE_EXPIRE_TIME)
                    .generateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();

            return Result.success(vo);
        }
    }

    /**
     * 验证注册码（不需要登录）
     * 用于用户注册时验证注册码，验证成功后直接删除
     * 失败时 Service 层会抛出异常，由全局异常处理器处理
     */
    @PostMapping("/validate")
    @Operation(summary = "验证注册码", description = "验证注册码是否有效，验证成功后直接删除（一次性使用）")
    public Result<Boolean> validateCode(@Valid @RequestBody ValidateCodeDTO validateCodeDTO) {
        log.info("验证注册码请求，注册码：{}", validateCodeDTO.getCode());

        // 验证并删除注册码（失败会抛出异常）
        registerCodeService.validateAndRemoveCode(validateCodeDTO.getCode());

        return Result.success(true);
    }

    /**
     * 检查注册码（不需要登录）
     * 仅检查有效性，不删除注册码
     * 失败时 Service 层会抛出异常，由全局异常处理器处理
     */
    @PostMapping("/check")
    @Operation(summary = "检查注册码", description = "检查注册码是否有效，不会删除验证码")
    public Result<Boolean> checkCode(@Valid @RequestBody ValidateCodeDTO validateCodeDTO) {
        log.info("检查注册码请求，注册码：{}", validateCodeDTO.getCode());

        // 验证注册码（不删除，失败会抛出异常）
        registerCodeService.validateCode(validateCodeDTO.getCode());

        return Result.success(true);
    }

    /**
     * 删除注册码（需要登录）
     * 生成者可以删除自己生成的注册码
     * 失败时 Service 层会抛出异常，由全局异常处理器处理
     */
    @DeleteMapping("/remove/{code}")
    @Operation(summary = "删除注册码", description = "删除指定的注册码（需要登录）")
    public Result<Boolean> removeCode(@PathVariable String code) {
        long userId = StpUtil.getLoginIdAsLong();
        log.info("删除注册码请求，用户ID：{}，注册码：{}", userId, code);

        // 删除注册码（失败会抛出异常）
        registerCodeService.removeCode(code);

        return Result.success(true);
    }

    /**
     * 查询注册码剩余有效时间（不需要登录）
     * 失败时 Service 层会抛出异常，由全局异常处理器处理
     */
    @GetMapping("/expire-time/{code}")
    @Operation(summary = "查询剩余有效时间", description = "查询注册码剩余有效时间（秒）")
    public Result<Long> getExpireTime(@PathVariable String code) {
        log.info("查询注册码剩余时间请求，注册码：{}", code);

        // 查询剩余时间（失败会抛出异常）
        long expireTime = registerCodeService.getCodeExpireTime(code);

        return Result.success(expireTime);
    }

    /**
     * 查询我生成的注册码列表（需要登录）
     * 失败时 Service 层会抛出异常，由全局异常处理器处理
     */
    @GetMapping("/my-codes")
    @Operation(summary = "查询我生成的注册码", description = "查询当前用户生成的注册码列表（需要登录）")
    public Result<List<String>> getMyCodes() {
        long userId = StpUtil.getLoginIdAsLong();
        log.info("查询用户生成的注册码列表，用户ID：{}", userId);

        // 查询注册码列表（失败会抛出异常）
        List<String> codes = registerCodeService.getGeneratorCodes(userId);

        return Result.success(codes);
    }

    /**
     * 查询注册码详细信息（需要登录）
     * 失败时 Service 层会抛出异常，由全局异常处理器处理
     */
    @GetMapping("/info/{code}")
    @Operation(summary = "查询注册码详细信息", description = "查询注册码的详细信息（需要登录）")
    public Result<Map<String, Object>> getCodeInfo(@PathVariable String code) {
        long userId = StpUtil.getLoginIdAsLong();
        log.info("查询注册码详细信息，用户ID：{}，注册码：{}", userId, code);

        // 查询注册码详细信息（失败会抛出异常）
        Map<String, Object> codeInfo = registerCodeService.getCodeInfo(code);

        return Result.success(codeInfo);
    }
}
