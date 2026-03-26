package com.tik.aipusharchiveservice.mapper;


import com.tik.aipusharchiveservice.bean.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PersonMapper {

    // 插入人员
    int insert(Person person);

    // 批量插入
    int batchInsert(List<Person> persons);

    // 根据ID更新
    int updateById(Person person);

    // 根据ID删除
    int deleteById(@Param("id") Long id);

    // 根据ID查询
    Person selectById(@Param("id") Long id);

    // 查询所有
    List<Person> selectAll();

    // 条件查询
    List<Person> selectByCondition(Person person);

    // 根据部门查询
    List<Person> selectByDepartment(@Param("department") String department);

    // 统计总数
    long count();
}
