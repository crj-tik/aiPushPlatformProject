package com.tik.aipushcommon.feign.archive;

import com.tik.aipushcommon.feign.ApiServiceNames;
import com.tik.aipushcommon.feign.archive.dto.PersonCreateRequest;
import com.tik.aipushcommon.feign.archive.dto.PersonQueryRequest;
import com.tik.aipushcommon.feign.archive.dto.PersonUpdateRequest;
import com.tik.aipushcommon.feign.archive.vo.PersonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        contextId = "archivePersonFeignClient",
        name = ApiServiceNames.ARCHIVE_SERVICE,
        path = "/api/persons"
)
public interface ArchivePersonFeignClient {

    @PostMapping
    PersonResponse create(@RequestBody PersonCreateRequest request);

    @PutMapping("/{id}")
    PersonResponse update(@PathVariable("id") Long id, @RequestBody PersonUpdateRequest request);

    @DeleteMapping("/{id}")
    void delete(@PathVariable("id") Long id);

    @GetMapping("/{id}")
    PersonResponse getById(@PathVariable("id") Long id);

    @GetMapping
    List<PersonResponse> getAll();

    @PostMapping("/search")
    List<PersonResponse> search(@RequestBody PersonQueryRequest request);

    @GetMapping("/department/{department}")
    List<PersonResponse> getByDepartment(@PathVariable("department") String department);

    @GetMapping("/count")
    long getCount();
}
