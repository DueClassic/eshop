package com.duoclassic.eshop.inventory.controller;

import com.duoclassic.eshop.inventory.model.ProductInventory;
import com.duoclassic.eshop.inventory.request.ProductInventoryCacheRefreshRequest;
import com.duoclassic.eshop.inventory.request.ProductInventoryDBUpdateRequest;
import com.duoclassic.eshop.inventory.request.Request;
import com.duoclassic.eshop.inventory.service.ProductInventoryService;
import com.duoclassic.eshop.inventory.service.RequestAsyncProcessService;
import com.duoclassic.eshop.inventory.vo.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 商品库存Controller
 */
@Controller
public class ProductInventoryController {

    @Resource
    private ProductInventoryService productInventoryService;
    @Resource
    private RequestAsyncProcessService requestAsyncProcessService;

    /**
     * 更新商品库存
     */
    @RequestMapping("/updateProductInventory")
    @ResponseBody
    public Response updateProductInventory(ProductInventory productInventory){
        System.out.println("===========日志===========: 接收到更新商品库存的请求，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getInventoryCnt());
        Response response=null;
        try {
            Request request=new ProductInventoryDBUpdateRequest(productInventory,productInventoryService);
            requestAsyncProcessService.handle(request);
            response=new Response(Response.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            response=new Response(Response.FAILURE);
        }
        return response;
    }

    /**
     * 获取商品库存
     */
    @RequestMapping("/getProductInventory")
    @ResponseBody
    public ProductInventory getProductInventory(Integer productId){
        System.out.println("===========日志===========: 接收到一个商品库存的读请求，商品id=" + productId);
        ProductInventory productInventory=null;
        try {
            Request request=new ProductInventoryCacheRefreshRequest(productId,productInventoryService,false);
            requestAsyncProcessService.handle(request);
            long startTime=System.currentTimeMillis();
            long waitTime=0L;
            long endTime=0L;

            while (true){
                //等待时间超过200ms，尝试去数据库中读取
                if (waitTime>200){
                    break;
                }
//                //Test
//                if (waitTime>20000){
//                    break;
//                }
                productInventory=productInventoryService.getProductInventoryCache(productId);
                if (productInventory!=null){
                    System.out.println("===========日志===========: 在200ms内读取到了redis中的库存缓存，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getInventoryCnt());
                    return productInventory;
                }else {
                    Thread.sleep(20);
                    endTime=System.currentTimeMillis();
                    waitTime=endTime-startTime;
                }
            }

            //直接从数据库中读取
            productInventory=productInventoryService.findProductInventory(productId);
            if (productInventory!=null){
                request = new ProductInventoryCacheRefreshRequest(
                        productId, productInventoryService, true);
                requestAsyncProcessService.handle(request);

                // 代码会运行到这里，只有三种情况：
                // 1、就是说，上一次也是读请求，数据刷入了redis，但是redis LRU算法给清理掉了，标志位还是false
                // 所以此时下一个读请求是从缓存中拿不到数据的，再放一个读Request进队列，让数据去刷新一下
                // 2、可能在200ms内，就是读请求在队列中一直积压着，没有等待到它执行（在实际生产环境中，基本是比较坑了）
                // 所以就直接查一次库，然后给队列里塞进去一个刷新缓存的请求
                // 3、数据库里本身就没有，缓存穿透，穿透redis，请求到达mysql库
                return productInventory;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ProductInventory(productId,-1L);
    }
}
