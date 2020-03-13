package com.example.demo.service.impl;

import com.example.demo.entity.Students;
import com.example.demo.mapper.StudentsMapper;
import com.example.demo.service.IStudentsService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 张海鹏
 * @since 2020-03-08
 */
@Service
public class StudentsServiceImpl extends ServiceImpl<StudentsMapper, Students> implements IStudentsService {

}
