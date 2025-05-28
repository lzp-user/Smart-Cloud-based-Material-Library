package com.lzp.lpicturebac.controller;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import com.lzp.lpicturebac.annotation.AuthCheck;
import com.lzp.lpicturebac.common.BaseResponse;
import com.lzp.lpicturebac.common.ResultUtils;
import com.lzp.lpicturebac.constant.UserConstant;
import com.lzp.lpicturebac.exception.BusinessException;
import com.lzp.lpicturebac.exception.ErrorCode;
import com.lzp.lpicturebac.manager.CosManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManager cosManager;

    /**
     * 测试文件上传
     *
     * @param multipartFile
     * @return
     */
    /*
    @RequestPart 是 Spring MVC 中用于处理 multipart/form-data 类型请求的注解，主要用于处理文件上传。
    它与 @RequestBody 的区别在于，@RequestPart 专门处理 multipart 请求中的 特定部分（part），可以是文件或其他表单字段。
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        // 文件目录
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s", filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);  //创建一个临时文件
            multipartFile.transferTo(file);  //把multipartFile赋值给临时文件
            cosManager.putObject(filepath, file);
            // 返回可访问的地址
            return ResultUtils.success(filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();  //转为流式输出
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);  //转为字节输出
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");  //告诉浏览器要下载
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            //attachment：强制浏览器下载文件，而非直接打开 filename：指定下载文件的名称。

            // 写入响应
            response.getOutputStream().write(bytes);  //写入数据
            response.getOutputStream().flush();
            //最后没有return返回给前段且返回参数是void,那前端如何接收到数据
            //使用servlet来接收HttpServletResponse response(最原始的方式 现在都不讲了)
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            // 释放流
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }

    }
}







