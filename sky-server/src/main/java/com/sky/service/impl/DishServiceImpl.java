package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增菜品和对应口味
     * @param dishDTO
     */
    @Override
    @Transactional  //事务注解
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //向菜品表插入一条数据
        dishMapper.insert(dish);
        Long dishId = dish.getId();
        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            // 插入数据
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult PageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Transactional  //事务注解，保证数据一致性
    @Override
    public void deleteBatche(List<Long> ids) {
        // 起售状态不能删除
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                // 当前菜品处于起售状态，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 套餐关联状态不能删除
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            // 有关联套餐，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品表中的菜品数据
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            //删除菜品关联的口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }

        dishMapper.deleteById(ids);
        dishFlavorMapper.deleteByDishId(ids);
    }

    /**
     * 菜品数据修改
     * @param dishDTO
     */
    @Override
    @Transactional
    public void reviseDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 修改基本的菜品表
        dishMapper.updata(dish);
        // 修改对应的口味表
        // 删除之前的口味列表
        List<Long> dishId = new ArrayList<>();
        dishId.add(dish.getId());
        dishFlavorMapper.deleteByDishId(dishId);
        // 插入现在的口味列表
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 根据id查询菜品及其关联的口味数据
     * @param id
     * @return
     */
    @Override
    public DishVO findByIdWithFlavor(Long id) {
        // 根据id查询菜品数据
        Dish dish = dishMapper.getById(id);
        // 根据菜品id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        // 封装到dishVo
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     */
    @Override
    @Transactional
    public void reviseStatus(Integer status, Long id) {
        // 构建Dish对象,直接使用updata对数据进行操作
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        // 修改菜品状态
        dishMapper.updata(dish);

        // 菜品对应的套餐状态，也要跟着改变
        if (status == StatusConstant.DISABLE) {
            // 将参数转变为List 可提高代码的复用性，方便以后实现多id修改
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            // 修改对应套餐为停售状态，先查询套餐id
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            if (setmealIds != null && setmealIds.size() > 0) {
                // 根据套餐id，修改套餐状态
                setmealIds.forEach(setmealId -> {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .build();
                    setmealMapper.update(setmeal);
                });
            }
        }
    }
}
