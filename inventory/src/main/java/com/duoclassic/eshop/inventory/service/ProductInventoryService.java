package com.duoclassic.eshop.inventory.service;

import com.duoclassic.eshop.inventory.model.ProductInventory;

/**
 * 商品库存Service接口
 */
public interface ProductInventoryService {

    /**
     * 更新商品库存
     * @param productInventory
     */
    void updateProductInventory(ProductInventory productInventory);

    /**
     * 删除Redis中的商品库存的缓存
     * @param productInventory
     */
    void removeProductInventory(ProductInventory productInventory);

    /**
     * 根据商品ID查询商品库存
     * @param productId
     * @return
     */
    ProductInventory findProductInventory(Integer productId);

    /**
     * 设置商品库存的缓存
     * @param productInventory
     */
    void setProductInventoryCache(ProductInventory productInventory);

    /**
     * 获取商品库存的缓存
     * @param productId
     * @return
     */
    ProductInventory getProductInventoryCache(Integer productId);
}
