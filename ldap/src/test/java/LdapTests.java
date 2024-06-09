import com.alibaba.fastjson.JSONObject;
import com.ldap.LdapApplication;
import com.base.entity.ldap.LdapPerson;
import com.base.service.ldap.LdapPersonService;
//import com.base.service.ldap.LdapPersonService_backup;
import com.pojo.vo.ldap.LdapPersonVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.ldap.LdapName;
import java.util.Enumeration;

@Slf4j
@SpringBootTest(classes = LdapApplication.class)
@RunWith(SpringRunner.class)
public class LdapTests {
/*
    @Autowired
    private LdapPersonService_backup ldapPersonService;*/

    @Autowired
    private LdapPersonService myLdapPersonService;

/*    @Test
    public void findAll() {
        ldapPersonService.findAll().forEach(p -> {
            System.out.println(p);
        });
    }*/

    @Test
    public void authenticate() {
        boolean ok = myLdapPersonService.authenticate("lisi20231108","123");
        log.debug("是否认证成功? "+ok);
    }

    @Test
    public void getPerson(){
        LdapPersonVO p = myLdapPersonService.getPerson("zhsan20231108");
        log.debug("json:"+JSONObject.toJSONString(p));
    }


}
