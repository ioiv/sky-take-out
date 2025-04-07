package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询:{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result deleteBatch(@RequestParam("ids") List<Long> ids){
        log.info("批量删除菜品:{}", ids);
        dishService.deleteBatche(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品及其关联的口味数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> findById(@PathVariable Long id){
        log.info("根据id查询菜品:{}", id);
        DishVO dishVO = dishService.findByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品数据
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品数据")
    public Result reviseDish(@RequestBody DishDTO dishDTO){
        log.info("修改菜品数据:{}", dishDTO);
        dishService.reviseDish(dishDTO);
        return Result.success();
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result reviseStatus(@PathVariable Integer status, @RequestParam(value = "id", required = false) Long id){
        log.info("修改菜品起售停售: {}, {}", status, id);
        dishService.reviseStatus(status,id);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(@RequestParam(value = "categoryId") Long categoryId){
        log.info("根据分类id查询菜品:{}", categoryId);
        List<Dish> dishList = dishService.list(categoryId);
        return Result.success(dishList);
    }
}
