package cn.fenqing168.utils.jsr303.demo;

import cn.fenqing168.utils.jsr303.bean.ErrorInfo;
import cn.fenqing168.utils.jsr303.code.Jsr303Utils;
import cn.fenqing168.utils.jsr303.handler.ValidationHandler;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author fenqing
 */
public class App {

    public static void main(String[] args) {
        User user = new User();
        save(user);
//        update(user);
    }

    public static void save(User user){
        List<ErrorInfo> errorInfos = Jsr303Utils.validation(user, Save.class);
        if(!errorInfos.isEmpty()){
            for (ErrorInfo errorInfo : errorInfos) {
                System.out.println(errorInfo.getField() + ":" + errorInfo.getMessage());
            }
            throw new RuntimeException("校验失败");
        }
    }

    public static void update(User user){
        List<ErrorInfo> errorInfos = Jsr303Utils.validation(user, Update.class);
        if(!errorInfos.isEmpty()){
            for (ErrorInfo errorInfo : errorInfos) {
                System.out.println(errorInfo.getField() + ":" + errorInfo.getMessage());
            }
            throw new RuntimeException("校验失败");
        }
    }

}

class User{
    @NotNull(message = "用户id不能为null", groups = {Update.class})
    private Long userId;
    @NotBlank(message = "名称不能为空", groups = {Update.class, Save.class})
    private String name;
    @NotBlank(message = "学校不能为null", groups = {Update.class, Save.class})
    private String school;
    @NotEmpty(message = "学科不能为空", groups = {Update.class, Save.class})
    private List<String> subject;
}

interface Save{}

interface Update{}