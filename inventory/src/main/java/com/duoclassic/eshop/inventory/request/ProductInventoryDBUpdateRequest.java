package com.duoclassic.eshop.inventory.request;

import com.duoclassic.eshop.inventory.model.ProductInventory;
import com.duoclassic.eshop.inventory.service.ProductInventoryService;

/**
 * cache aside pattern
 * 1.删除缓存
 * 2.更新数据库
 */
public class ProductInventoryDBUpdateRequest implements Request{

    /**
     * 商品库存
     */
    private ProductInventory productInventory;
    /**
     * 商品库存Service
     */
    private ProductInventoryService productInventoryService;

    public ProductInventoryDBUpdateRequest(ProductInventory productInventory,
                                       ProductInventoryService productInventoryService){
        this.productInventory=productInventory;
        this.productInventoryService=productInventoryService;
    }

    /**
     * 1.删除缓存
     * 2.更新数据库
     */
    @Override
    public void process() {
        System.out.println("===========日志===========: 数据库更新请求开始执行，" +
                "商品id=" + productInventory.getProductId() +
                ", 商品库存数量=" + productInventory.getInventoryCnt());
        //1.删除redis缓存
        productInventoryService.removeProductInventory(productInventory);
        //Test
//        try {
//            Thread.sleep(10000);
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }
        //2.修改数据库
        productInventoryService.updateProductInventory(productInventory);
    }

    @Override
    public Integer getProductId() {
        return productInventory.getProductId();
    }

    @Override
    public boolean isForceRefresh() {
        return false;
    }
}
