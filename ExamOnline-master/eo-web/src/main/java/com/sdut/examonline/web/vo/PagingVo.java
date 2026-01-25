package com.sdut.examonline.web.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Chen
 * @since 2022/3/11 17:53
 */
@ApiModel(value = "分页数据", description = "分页数据")
@Data
@NoArgsConstructor
public class PagingVo<T> extends BaseVo {

    @ApiModelProperty(value = "数据")
    private List<T> data;

    @ApiModelProperty(value = "数据总数")
    private Long total = 0L;

    @ApiModelProperty(value = "当前页")
    private Long current = 0L;

    @ApiModelProperty(value = "总页数")
    private Long pages = 0L;

    public PagingVo(IPage<T> page) {
        this.data = page.getRecords();
        this.total = page.getTotal();
        this.current = page.getCurrent();
        this.pages = page.getPages();
    }

    public static <T> PagingVo<T> of(List<T> data, long total, long current, long pages) {
        PagingVo<T> pagingVo = new PagingVo<>();
        pagingVo.data = data;
        pagingVo.total = total;
        pagingVo.current = current;
        pagingVo.pages = pages;
        return pagingVo;
    }

}
