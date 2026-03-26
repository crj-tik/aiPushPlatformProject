package com.tik.aipushpushservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 数据摄取服务 - 负责将各种数据源写入 Milvus
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataIngestionService {

    private final VectorStore vectorStore;              // Milvus 向量存储
    private final JdbcTemplate jdbcTemplate;            // 用于查询数据库表
    private final RestTemplate restTemplate;            // 用于调用业务接口

    /**
     * 将数据库表写入向量库
     * @param tableName 表名
     * @param idColumn 主键列
     * @param contentColumns 需要向量化的内容列（可多个）
     * @param metadataColumns 作为元数据的列
     */
    public void ingestDatabaseTable(String tableName,
                                    String idColumn,
                                    List<String> contentColumns,
                                    List<String> metadataColumns) {
        log.info("开始将数据库表 {} 写入向量库", tableName);

        // 1. 构建查询 SQL
        String columns = String.join(", ",
                new ArrayList<String>() {{
                    add(idColumn);
                    addAll(contentColumns);
                    addAll(metadataColumns);
                }}
        );

        String sql = String.format("SELECT %s FROM %s", columns, tableName);

        // 2. 查询数据
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        log.info("查询到 {} 条记录", rows.size());

        // 3. 转换为 Document 并存入向量库
        List<Document> documents = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            // 构建文档内容（将多个内容列拼接）
            StringBuilder content = new StringBuilder();
            for (String col : contentColumns) {
                content.append(row.get(col)).append(" ");
            }

            // 构建元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("source_type", "database");
            metadata.put("source_table", tableName);
            metadata.put("record_id", row.get(idColumn));

            for (String col : metadataColumns) {
                metadata.put(col, row.get(col));
            }

            // 创建 Document（ID 用表名+主键确保唯一）
            String docId = tableName + "_" + row.get(idColumn);
            Document doc = new Document(docId, content.toString().trim(), metadata);
            documents.add(doc);

            // 批量插入，每 100 条提交一次
            if (documents.size() >= 100) {
                vectorStore.add(documents);
                log.info("已写入 {} 条记录", documents.size());
                documents.clear();
            }
        }

        // 写入剩余记录
        if (!documents.isEmpty()) {
            vectorStore.add(documents);
            log.info("最终写入 {} 条记录", documents.size());
        }

        log.info("数据库表 {} 写入完成", tableName);
    }

    /**
     * 将业务接口数据写入向量库
     * @param apiUrl 接口地址
     * @param dataProcessor 数据处理函数（将接口返回数据转为 Document）
     */
    public void ingestApiData(String apiUrl,
                              java.util.function.Function<Map<String, Object>, Document> dataProcessor) {
        log.info("开始从接口 {} 摄取数据", apiUrl);

        try {
            // 1. 调用接口获取数据
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

            if (response == null || !response.containsKey("data")) {
                log.warn("接口返回数据为空");
                return;
            }

            List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");

            // 2. 转换为 Document
            List<Document> documents = new ArrayList<>();
            for (Map<String, Object> item : dataList) {
                Document doc = dataProcessor.apply(item);
                if (doc != null) {
                    documents.add(doc);
                }
            }

            // 3. 存入向量库
            vectorStore.add(documents);
            log.info("从接口 {} 摄取了 {} 条数据", apiUrl, documents.size());

        } catch (Exception e) {
            log.error("接口数据摄取失败: {}", apiUrl, e);
        }
    }

    /**
     * 将知识文档写入向量库（使用 Spring AI 的文档读取器）
     * @param filePath 文件路径
     * @param fileType 文件类型（pdf/word/txt等）
     */
    public void ingestDocument(String filePath, String fileType) {
        log.info("开始处理文档: {}", filePath);

        try {
            // 根据文件类型选择不同的读取器
            List<Document> documents;

            if ("pdf".equalsIgnoreCase(fileType)) {
                // 使用 TikaDocumentReader 读取 PDF（Spring AI 内置）
                // 这里需要引入 spring-ai-tika-document-reader 依赖
                documents = readPdf(filePath);
            } else if ("txt".equalsIgnoreCase(fileType)) {
                // 使用 TextReader 读取文本文件
                documents = readText(filePath);
            } else {
                log.warn("不支持的文件类型: {}", fileType);
                return;
            }

            // 文档切分（避免超长）
            List<Document> chunks = splitDocuments(documents);

            // 添加元数据
            for (Document doc : chunks) {
                doc.getMetadata().put("source_type", "document");
                doc.getMetadata().put("source_file", filePath);
                doc.getMetadata().put("file_type", fileType);
            }

            // 存入向量库
            vectorStore.add(chunks);
            log.info("文档 {} 处理完成，生成 {} 个文档块", filePath, chunks.size());

        } catch (Exception e) {
            log.error("文档处理失败: {}", filePath, e);
        }
    }

    /**
     * 文档切分（避免超长文本）
     */
    private List<Document> splitDocuments(List<Document> docs) {
        if (docs == null || docs.isEmpty()) {
            return new ArrayList<>();
        }

        // 创建分割器：chunkSize=500, chunkOverlap=50 (通过minChunkSizeChars控制)
        TokenTextSplitter splitter = new TokenTextSplitter(500, 50, 5, 10000, true);

        try {
            List<Document> splitDocs = splitter.apply(docs);
            log.info("文档切分完成：{} 个原始文档 -> {} 个文档块", docs.size(), splitDocs.size());
            return splitDocs;
        } catch (Exception e) {
            log.error("文档切分失败，返回原始文档", e);
            return docs; // 失败时返回原文档作为降级
        }
    }

    // 以下为简化实现，实际需引入相应依赖
    private List<Document> readPdf(String filePath) {
        // 实际应用需引入 spring-ai-tika-document-reader
        return Collections.emptyList();
    }

    private List<Document> readText(String filePath) {
        // 使用 Spring AI 的 TextReader
        return Collections.emptyList();
    }
}
