package jiang.kaka.model.bean;

/** 群消息的bean类
 * Created by 在云端 on 2016/10/28.
 */
public class GroupInfo {
    private String groupName;
    private String groudId;
    private String invatePerson;//邀请人

    public GroupInfo() {
    }

    public GroupInfo(String groupName, String groudId, String invatePerson) {
        this.groupName = groupName;
        this.groudId = groudId;
        this.invatePerson = invatePerson;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroudId() {
        return groudId;
    }

    public void setGroudId(String groudId) {
        this.groudId = groudId;
    }

    public String getInvatePerson() {
        return invatePerson;
    }

    public void setInvatePerson(String invatePerson) {
        this.invatePerson = invatePerson;
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
                "groupName='" + groupName + '\'' +
                ", groudId='" + groudId + '\'' +
                ", invatePerson='" + invatePerson + '\'' +
                '}';
    }
}
