package com.base.service.ldap;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.entity.ldap.LdapPerson;
import com.base.entity.ldap.SysLdapUser;
import com.base.mapper.LdapUserMapper;
import com.common.util.EncryptionUtil;
import com.pojo.vo.ldap.LdapPersonVO;
import org.springframework.beans.BeanUtils;
import org.springframework.ldap.core.*;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.naming.ldap.LdapName;


@Service
public class LdapPersonService extends ServiceImpl<LdapUserMapper, SysLdapUser> {

    @Resource
    private LdapTemplate ldapTemplate;

   /* @Autowired
    private LdapPersonService_backup ldapPersonService_backup;*/

    /**
     * ldap用户身份认证
     * @param userId
     * @param password
     * @return
     */
    public boolean authenticate(String userId, String password) {
        EqualsFilter filter = new EqualsFilter("uid", userId);
        return ldapTemplate.authenticate("", filter.toString(), password);
    }

    /**
     * 根据uid查询用户
     * @param userId
     * @return
     */
    public LdapPersonVO getPerson(String userId){
        //EqualsFilter filter = new EqualsFilter("sAMAccountName", "yaozong.li");
        LdapQuery query = LdapQueryBuilder.query()
                .where("uid").is(userId);
        LdapPerson person = ldapTemplate.findOne(query, LdapPerson.class);

        LdapPersonVO personVO = new LdapPersonVO();
        BeanUtils.copyProperties(person,personVO,"id");
        LdapName ldapName = (LdapName)person.getId();
        personVO.setId(ldapName.toString());

        //springSecurity自动将密码转换成了ascll码数组的形式,所以需要将ascll数组转换为字符串
        String [] ascllArray = personVO.getUserPassword().split(",");
        String password = EncryptionUtil.convertToString(ascllArray);
        personVO.setUserPassword(password);

        //注意，查找不到对应的用户时会抛出EmptyResultDataAccessException的运行时异常
        return personVO;
    }

}
