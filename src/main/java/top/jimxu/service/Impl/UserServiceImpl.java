package top.jimxu.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.jimxu.entity.domain.ComUser;
import top.jimxu.entity.domain.User;
import top.jimxu.mapper.ComUserMapper;
import top.jimxu.mapper.UserMapper;
import top.jimxu.service.UserService;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private ComUserMapper comUserMapper;

    @Override
    public int insertUser(User user) {
        return baseMapper.insert(user);
    }

    @Override
    public int updateUser(User user) {
        return baseMapper.updateById(user);
    }

    @Override
    public int deleteUser(User user) {
        return baseMapper.deleteById(user.getUserId());
    }

    @Override
    public User findUserById(int id) {
        System.out.println(id);
        System.out.println(baseMapper.selectById(id));
        return baseMapper.selectById(id);
    }

    @Override
    public List<User> selectList(User user) {
        return baseMapper.selectList(null);
    }

    @Override
    public List<ComUser> getNeedUpdatePasswordUser() {
        return comUserMapper.getNeedUpdatePasswordUser();
    }

    @Override
    public void updateComUser(ComUser comUser) {
        comUserMapper.updateById(comUser);
    }
}
