
1）需要有一个用户
2）给用户配置权限代码（权限code）
3）给每个权限标识符配置能访问的资源


### 1.需要有一个用户

```java
package com.powernode.service.impl;  
  
import com.powernode.mapper.PermissionMapper;  
import com.powernode.mapper.RoleMapper;  
import com.powernode.mapper.UserMapper;  
import com.powernode.pojo.Permission;  
import com.powernode.pojo.Role;  
import com.powernode.pojo.User;  
import com.powernode.service.UserService;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.security.core.userdetails.UserDetails;  
import org.springframework.security.core.userdetails.UsernameNotFoundException;  
import org.springframework.stereotype.Service;  
  
import java.util.List;  
  
@Service  
public class UserServiceImpl implements UserService {  
  
    @Autowired  
    private UserMapper userMapper;  
  
    @Autowired  
    private PermissionMapper permissionMapper;  
    @Override  
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  
        User user = userMapper.selectByLoginAct(username);  
        if (user == null) {  
            throw new UsernameNotFoundException("账户不存在");  
        }  
        List<Permission> permissions = permissionMapper.selectByUserId(user.getId());  
        user.setPermissionList(permissions);  
        return user;  
    }  
}
```

由于要调用User表中setPermissionList，所以我们要在User表中新加字段

```java
@JsonIgnore  
private List<Permission> permissionList;
```

重写getAuthorities()方法

```java
@Override  
public Collection<? extends GrantedAuthority> getAuthorities() {  
    Collection<GrantedAuthority> authorities = new ArrayList<>();  
    for (Permission permission : this.permissionList) {  
        // 放入角色  
        //authorities.add(new SimpleGrantedAuthority("ROLE_"+ role.getRole()));  
        // 放入权限（权限标识符，权限code，权限代码）  
        authorities.add(new SimpleGrantedAuthority(permission.getCode()));  
    }  
  
    return authorities;  
}
```

### 使用逆向工程创建Permission

![premission的逆向工程|264](SpringSecurity/图片/004.png)

### 创建ClueController
```java
package com.powernode.controller;  
  
import org.springframework.security.access.prepost.PreAuthorize;  
import org.springframework.stereotype.Controller;  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RestController;  
  
@RestController  
public class ClueController {  
  
    /**  
     * clue:list     * clue:add     * clue:edit     * clue:view     * clue:import     * customer:list     * customer:view     * customer:export     * tran:list     * tran:view     * system:list     * @return     */    @RequestMapping("/api/clue/index")  
    public String clueIndex(){  
        return "clueIndex";  
    }  
  
    @RequestMapping("/api/clue/menu")  
    public String clueMenu(){  
        return "clueMenu";  
    }  
    @RequestMapping("/api/clue/menu/child")  
    public String clueMenuChild(){  
        return "clueMenuChild";  
    }  
  
    @PreAuthorize("hasAuthority('clue:list')")  
    @RequestMapping("/api/clue/list")  
    public String clueList(){  
        return "clueList";  
    }  
  
    @PreAuthorize("hasAuthority('clue:add')")  
    @RequestMapping("/api/clue/input")  
    public String clueInput(){  
        return "clueInput";  
    }  
  
    @PreAuthorize("hasAuthority('clue:edit')")  
    @RequestMapping("/api/clue/edit")  
    public String clueEdit(){  
        return "clueEdit";  
    }  
  
    @PreAuthorize("hasAuthority('clue:view')")  
    @RequestMapping("/api/clue/view")  
    public String clueView(){  
        return "clueView";  
    }  
  
    @PreAuthorize("hasAuthority('clue:del')")  
    @RequestMapping("/api/clue/delete")  
    public String clueDel(){  
        return "clueDel";  
    }  
  
    @PreAuthorize(value = "hasAnyAuthority('clue:export','clue:download')")  
    @RequestMapping("/api/clue/export")  
    public String clueExport(){  
        return "clueExport";  
    }  
  
  
}
```



