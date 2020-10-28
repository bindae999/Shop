package mydelivery;

public class DeliveryRequested extends AbstractEvent {

    private Long deliveryid;
    private Long orderId;
    private String orderStatus;

    public Long getId() {
        return deliveryid;
    }

    public void setId(Long deliveryid) {
        this.deliveryid = deliveryid;
    }
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public String getOrderSatus() {
        return orderStatus;
    }

    public void setOrderSatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}