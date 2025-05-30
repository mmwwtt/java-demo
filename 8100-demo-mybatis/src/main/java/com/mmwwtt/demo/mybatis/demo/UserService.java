package com.mmwwtt.demo.mybatis.demo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Resource
    private UserDAO userDao;


    public List<User> query(User query){
        List<User> list1 = userDao.query1(query);
        List<User> list = userDao.query(query);
        return list;
    }


    public PageInfo<User> queryPage(User query, int pageNum, int pageSize) {
        //PageHelper.startPage 自动拦截sql,计算总数，添加分页条件（limit 起点，页大小）
        PageHelper.startPage(pageNum, pageSize);
        //List<User> list1 = userDao.query1(query);
        List<User> list = userDao.query(query);
        return new PageInfo<>(list);
    }

    public User save(User user) {
        userDao.save(user);
        return user;
    }
    public List<User> saveAll(List<User> list) {
        userDao.saveAll(list);
        return list;
    }

    public User update(User user) {
        userDao.update(user);
        return user;
    }
    public List<User> updateAll(List<User> list) {
        userDao.updateAll(list);
        return list;
    }

}
