package com.duoclassic.eshop.inventory.mapper;

import com.duoclassic.eshop.inventory.model.ProductInventory;
import org.apache.ibatis.annotations.Param;

/**
 * 库存数量mapper
 */
public interface ProductInventoryMapper {

    /**
     * 更新库存数量
     * @param productInventory
     */
    void updateProductInventory(ProductInventory productInventory);

    /**
     * 根据商品ID查询商品库存信息
     * @param productId
     * @return  商品库存信息
     */
    ProductInventory findProductInventory(@Param("productId") Integer productId);
}
