package com.recording;

/*
 * 或运算特点:
 *           有1就是1,两个都是0才是0
 *       例子:
 *           0101
 *           0011
 *           0111    结果
 * 与运算特点:
 *           有0就是0,两个都是1才是1
 *       例子:
 *           0101
 *           0011
 *           0001    结果
 *  ~运算 :
 *           将0变为1,1变为0
 *      例子:
 *           0101
 *           结果: 1010
 *
 * */
/**
 * 测试练习类,默认有四种权限供设置
 * 权限以二进制数的每位的1为标准
 * 2019.11.22
 */
class Permission{
    private static final int ALLOW_UPDATE = 1 << 0;     //0001      1
    private static final int ALLOW_INSERT = 1 << 1;     //0010      2
    private static final int ALLOW_QUERY = 1 << 2;      //0100      4
    private static final int ALLOW_DELETE = 1 << 3;     //1000      8
    private int allow;      //用户当前拥有的权限

    /*给用户设置一个权限*/
    public void setAllow(int allow){
        this.allow = allow;
    }

    /*给用户新增一个权限(也可能是新赋予用户好几个权限)*/
    public void addAllow(int newAllow){
        allow = allow | newAllow;

        /*
         * 增加权限,意为将某一位变为1,所以不能影响原因的权限,所有不能更改原有位置的1,就用容易产生1的方式,或运算
         * */
    }

    //去掉用户拥有的一个权限(或去掉多个)
    public void delAllow(int delAllow){
        allow = allow & ~delAllow;
    }

    //检查用户是否拥有参数代表的权限
    public boolean checkAllow(int allow){
        return (this.allow & allow) == allow;
    }

    //main方法进行测试
    public static void main(String[] args) {
        int selfAllow = 1;
        Permission permission = new Permission();
        permission.setAllow(selfAllow);

        /*测试给用户赋予更多的权限*/
        permission.addAllow(Permission.ALLOW_DELETE | Permission.ALLOW_INSERT |
                Permission.ALLOW_QUERY | Permission.ALLOW_UPDATE);

        /*测试用户是否拥有所有的权限*/
        System.out.println(permission.checkAllow(Permission.ALLOW_DELETE | Permission.ALLOW_INSERT |
                Permission.ALLOW_QUERY | Permission.ALLOW_UPDATE));
    }

}
