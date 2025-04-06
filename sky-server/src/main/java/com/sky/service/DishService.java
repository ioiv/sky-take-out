package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     *新增菜品和对应口味
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult PageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除
     * @param ids
     */
    void deleteBatche(List<Long> ids);

    /**
     * 菜品数据修改
     * @param dishDTO
     */
    void reviseDish(DishDTO dishDTO);

    /**
     * 根据id查询菜品及其关联的口味数据
     *
     * @param id
     * @return
     */
    DishVO findByIdWithFlavor(Long id);

    /**
     * 修改菜品状态 起售，停售
     * @param status
     * @param id
     */
    void reviseStatus(Integer status, Long id);
}
