package com.mmwwtt.demo.elasticsearch.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class DemoTest {


//    @Resource
//    private DogRepository dogRepository;
//
//    @Test
//    void testFindByName() {
//        List<Dog> list = dogRepository.findByName("欢欢");
//
//        System.out.println("共查询到" + list.size() + "条记录。");
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//
//        for (Dog dog : list) {
//            log.info("{}",dog);
//        }
//    }
//
//    @Test
//    void testSave() {
//        Dog dog = new Dog();
//        dog.setName("雨爱");
//        dog.setSex("1");
//
//        Dog dogSave = dogRepository.save(dog);
//        log.info("save = {}", dogSave);
//    }
//
//    @Test
//    void testDelete() {
//        Dog dog = new Dog();
//        dog.setName("雨爱");
//        dog.setSex("男");
//
//        dogRepository.delete(dog);
//    }
//
//    @Test
//    void testDeleteById() {
//        testSave();
//        testFindByName();
//
//        dogRepository.deleteById("2023");
//
//        testFindByName();
//    }
//
//    @Test
//    void testDeleteAll() {
//        dogRepository.deleteAll();
//    }
//
//    @Test
//    void testExistsById() {
//        boolean exists = dogRepository.existsById("2023");
//
//        if (!exists) {
//            System.out.println("记录不存在");
//        }
//    }
//
//    @Test
//    void testFindById() {
//        testSave();
//
//        Optional<Dog> optional = dogRepository.findById("2023");
//
//        if (optional.isPresent()) {
//            Dog dog = optional.get();
//
//            log.info("{}", dog);
//        }
//    }
//
//    @Test
//    void testCount() {
//        long count = dogRepository.count();
//
//        log.info("count: {}",count);
//    }

}
