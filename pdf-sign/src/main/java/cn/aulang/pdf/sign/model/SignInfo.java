package cn.aulang.pdf.sign.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Aulang
 * @date 2021-01-16 20:46
 */
@Data
@Builder
public class SignInfo {
    /**
     * 盖章日期，可为null
     */
    Long signDate;
    /**
     * 盖章理由，可为null
     */
    String reason;
    /**
     * 盖章位置，可为null
     */
    String location;
    /**
     * 盖章人，可为null
     */
    String creator;
    /**
     * 盖章人联系方式，可为null
     */
    String contact;
    /**
     * 盖章关键字查找页面，负数倒数页
     */
    private List<Integer> pages;
    /**
     * 盖章关键字查找正则表达式
     */
    private String regex;
    /**
     * 是否最后一个关键字处盖章，TODO 暂不支持一次盖多个章
     */
    private Boolean latest;
}
