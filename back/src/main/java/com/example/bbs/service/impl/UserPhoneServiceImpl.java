package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.UserManageMapper;
import com.example.bbs.mapper.UserMapper;
import com.example.bbs.mapper.UserPhoneMapper;
import com.example.bbs.mapper.UserSchoolMapper;
import com.example.bbs.pojo.*;
import com.example.bbs.service.UserPhoneService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/3 10:56 上午
 */
@Service
public class UserPhoneServiceImpl implements UserPhoneService {
    @Autowired
    private UserManageMapper userManageMapper;


    @Autowired
    UserMapper userMapper;

    @Autowired
    UserSchoolMapper userSchoolMapper;

    @Autowired
    UserPhoneMapper userPhoneMapper;


    //新增单条关联记录
    @Override
    public int createUserPhoneRecord(User user) {
        //检测有没有存过
        QueryWrapper<UserPhone> userPhoneQueryWrapper = new QueryWrapper<>();
        userPhoneQueryWrapper.eq("user_id", user.getUserId());
        List<UserPhone> userPhoneList =userPhoneMapper.selectList(userPhoneQueryWrapper);
        if (userPhoneList!=null && userPhoneList.size()>0){
            return 2;
        }
        //没有存过
        UserPhone userPhone = new UserPhone();
        String newUUID = UUID.randomUUID().toString().replace("-", "");
        Date nowDate = new Date();
        userPhone.setId(newUUID);
        userPhone.setUserId(user.getUserId());
        userPhone.setEncryptedPhone(user.getPhone());
        String decryptedText = decryptPhoneData(user.getPhone());
        userPhone.setPhone(maskPhoneNumber(decryptedText));
        userPhone.setCreateTime(nowDate);
        userPhone.setUpdateTime(nowDate);

        return userPhoneMapper.insert(userPhone);
    }


    private static final String privateKeyString ="";
    @SneakyThrows
    private String decryptPhoneData(String phone){
        // 将私钥字符串转换为字节数组
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        // 使用PKCS8EncodedKeySpec构建私钥对象
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSASSA-PSS");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // 创建RSA解密器，确保填充方式与前端一致
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // 将Base64解码后的数据分块解密
        byte[] encryptedData = Base64.getDecoder().decode(phone);
        int blockSize = ((RSAPrivateKey) privateKey).getModulus().bitLength() / 8;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (int i = 0; i < encryptedData.length; i += blockSize) {
            byte[] block = cipher.doFinal(encryptedData, i, Math.min(blockSize, encryptedData.length - i));
            outputStream.write(block);
        }

        // 获取解密后的数据
        String decryptedText = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        return  decryptedText;
    }

    // 将电话号码的中间四位变为****
    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 7) {
            throw new IllegalArgumentException("电话号码格式不正确");
        }

        // 提取电话号码的前三位和后四位
        String firstPart = phoneNumber.substring(0, 3);
        String lastPart = phoneNumber.substring(phoneNumber.length() - 4);

        // 返回掩码处理后的电话号码
        return firstPart + "****" + lastPart;
    }
}
