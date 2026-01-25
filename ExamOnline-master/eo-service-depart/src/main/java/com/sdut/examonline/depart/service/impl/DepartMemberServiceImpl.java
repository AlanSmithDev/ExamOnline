package com.sdut.examonline.depart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.examonline.depart.entity.DepartMember;
import com.sdut.examonline.depart.mapper.DepartMemberMapper;
import com.sdut.examonline.depart.service.DepartMemberService;
import org.springframework.stereotype.Service;

/**
 * @author Chen
 * @since 2022/3/18 19:25
 */
@Service
public class DepartMemberServiceImpl extends ServiceImpl<DepartMemberMapper, DepartMember> implements DepartMemberService {
}
