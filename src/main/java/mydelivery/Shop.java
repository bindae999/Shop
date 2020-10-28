package mydelivery;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Shop_table")
public class Shop {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long shopId;
    private Long orderId;
    private String foodName;
    private Long foodQty;
    private String orderStatus;

    @PostUpdate
    public void onPostUpdate(){
        RequestCanceled requestCanceled = new RequestCanceled();
        BeanUtils.copyProperties(this, requestCanceled);

        if("request Cancel".equals(requestCanceled.getOrderStatus())){
            requestCanceled.setOrderStatus(this.getOrderStatus());
            requestCanceled.publishAfterCommit();
        }else{
            DeliveryRequested deliveryRequested = new DeliveryRequested();
            BeanUtils.copyProperties(this, deliveryRequested);
            deliveryRequested.publishAfterCommit();

            //Following code causes dependency to external APIs
            // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
            mydelivery.external.Delivery delivery = new mydelivery.external.Delivery();
            System.out.println("배달요청 접수!!"+this.getOrderId());
            delivery.setOrderId(this.getOrderId());
            delivery.setOrderStatus("Delivery Request");

            // mappings goes here
            ShopApplication.applicationContext.getBean(mydelivery.external.DeliveryService.class)
                    .delivery(delivery);

        }
    }


    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
    public Long getFoodQty() {
        return foodQty;
    }

    public void setFoodQty(Long foodQty) {
        this.foodQty = foodQty;
    }
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }




}
