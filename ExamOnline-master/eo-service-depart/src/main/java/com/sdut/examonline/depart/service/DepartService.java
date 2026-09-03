package com.sdut.examonline.depart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.depart.dto.*;
import com.sdut.examonline.depart.entity.Depart;
import com.sdut.examonline.depart.vo.DepartDetailVo;
import com.sdut.examonline.depart.vo.DepartMemberVo;
import com.sdut.examonline.depart.vo.DepartVo;
import com.sdut.examonline.dubbo.dto.DepartMembersDto;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.vo.PagingVo;

import java.util.List;
import java.util.Map;

public interface DepartService extends IService<Depart> {

    /**
     * 添加班级
     *
     * @param createRequest 创建请求
     */
    DepartVo add(DepartCreateRequest createRequest);

    /**
     * 保存班级信息
     *
     * @param saveRequest 保存请求
     */
    DepartVo save(DepartSaveRequest saveRequest);

    /**
     * 删除班级
     *
     * @param departId 班级ID
     */
    Boolean delete(Long departId);

    /**
     * 获取班级信息
     *
     * @param departId 班级ID
     */
    DepartVo getDepart(Long departId);

    /**
     * 加入班级
     *
     * @param code 班级码
     */
    DepartVo join(String code);

    /**
     * 获取我的班级信息
     */
    List<DepartVo> myDepart();

    /**
     * 获取用户班级信息
     *
     * @param userId 用户ID
     */
    List<DepartVo> getDepartsByUserId(Long userId);

    /**
     * 获取班级所有成员ID(内部调用)
     */
    List<Long> getDepartMemberIdsInner(Long departId);

    /**
     * 分页获取班级列表
     *
     * @param pagingDto 分页请求
     */
    PagingVo<DepartVo> page(PagingDto pagingDto);

    /**
     * 获取所有树型班级
     */
    List<DepartVo> all();

    /**
     * 获取班级及成员信息
     *
     * @param departId 班级ID
     */
    DepartDetailVo detail(Long departId);

    /**
     * 添加班级成员
     *
     * @param request 请求
     */
    DepartMemberVo add(DepartMemberAddRequest request);

    /**
     * 批量添加班级成员
     *
     * @param request 请求
     */
    List<DepartMemberVo> addBatch(DepartMemberAddBatchRequest request);

    /**
     * 删除班级成员
     *
     * @param request 请求
     */
    Boolean remove(DepartMemberRemoveRequest request);

    /**
     * 批量删除班级成员
     *
     * @param request 请求
     */
    Boolean removeBatch(DepartMemberRemoveBatchRequest request);

    /**
     * 获取班级成员及间接(Dubbo)
     *
     * @param departId 班级ID
     */
    DepartMembersDto getMembersDubbo(Long departId);

    /**
     * 获取班级成员及间接(Dubbo)
     *
     * @param departIds 班级ID列表
     */
    Map<String, DepartMembersDto> getMembersBatchDubbo(List<Long> departIds);

}
