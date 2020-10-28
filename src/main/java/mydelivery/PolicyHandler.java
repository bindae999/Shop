package mydelivery;

import mydelivery.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Optional;

@Service
public class PolicyHandler{

    @Autowired
    ShopRepository shopRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderRequested_CreateOrder(@Payload OrderRequested orderRequested){

        if(orderRequested.isMe()){
            System.out.println("##### listener CreateOrder : " + orderRequested.toJson());
            int flag=0;

            //pss : 오더접수 수신시 객체 생성(update)
            Iterator<Shop> iterator = shopRepository.findAll().iterator();
            while(iterator.hasNext()){
                Shop shopTmp = iterator.next();
                if(shopTmp.getOrderId() == orderRequested.getOrderId()){
                    System.out.println("$$$$$"+shopTmp.getOrderId());
                    flag=1;
                    Optional<Shop> ShopOptional = shopRepository.findById(shopTmp.getOrderId());
                    Shop shop = ShopOptional.get();
                    shop.setOrderStatus(orderRequested.getOrderStatus());
                    shopRepository.save(shop);
                }
            }

            //pss : 기존오더가 없다면 신규생성(insert)
            if (flag==0){
                System.out.println("신규 접수!");
                Shop shop = new Shop();
                shop.setOrderStatus(orderRequested.getOrderStatus());
                shop.setFoodName(orderRequested.getFoodName());
                shop.setFoodQty(orderRequested.getFoodQty());
                shop.setOrderId(orderRequested.getOrderId());
                shopRepository.save(shop);
            }

        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCanceled_CancelOrder(@Payload OrderCanceled orderCanceled){

        if(orderCanceled.isMe()){
            System.out.println("##### listener CancelOrder : " + orderCanceled.toJson());

            //pss : 오더취소 수신시 객체 삭제(delete)
            Iterator<Shop> iterator = shopRepository.findAll().iterator();
            while(iterator.hasNext()){
                Shop shopTmp = iterator.next();
                if(shopTmp.getOrderId() == orderCanceled.getOrderId()){
                    Optional<Shop> ShopOptional = shopRepository.findById(shopTmp.getOrderId());
                    Shop shop = ShopOptional.get();
                    System.out.println("########"+orderCanceled.getOrderStatus());
                    if(orderCanceled.getOrderStatus().equals("Cancel")){
                        shopRepository.delete(shop);
                    }

                }
            }

        }
    }

}
