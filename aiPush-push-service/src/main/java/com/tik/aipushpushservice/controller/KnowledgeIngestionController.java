package com.tik.aipushpushservice.controller;

import com.tik.aipushpushservice.service.impl.DataIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeIngestionController {

    @Autowired
    private DataIngestionService ingestionService;

    @PostMapping("/upload")
    public String uploadKnowledge(@RequestParam("file") MultipartFile file) {
        try {
            // 保存临时文件
            String tempPath = "/tmp/" + file.getOriginalFilename();
            file.transferTo(new File(tempPath));

            // 摄取文档
            String fileType = file.getOriginalFilename().substring(
                    file.getOriginalFilename().lastIndexOf(".") + 1
            );
            ingestionService.ingestDocument(tempPath, fileType);

            return "文档上传并处理成功";
        } catch (Exception e) {
            return "处理失败: " + e.getMessage();
        }
    }
}
